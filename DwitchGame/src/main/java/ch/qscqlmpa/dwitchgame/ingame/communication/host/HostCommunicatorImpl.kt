package ch.qscqlmpa.dwitchgame.ingame.communication.host

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.CommServer
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.model.Recipient
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@InGameScope
internal class HostCommunicatorImpl @Inject constructor(
    private val commServer: CommServer,
    private val communicationEventDispatcher: HostCommunicationEventDispatcher,
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val schedulerFactory: SchedulerFactory
) : HostCommunicator, ComputerCommunicator {

    private val disposableManager = DisposableManager()

    private val hostReceivedMessagesRelay = PublishRelay.create<ServerEvent.EnvelopeReceived>()
    private val hostCommunicationEventsRelay = PublishRelay.create<ServerEvent.CommunicationEvent>()
    private val messagesToComputerPlayersRelay = PublishRelay.create<EnvelopeToSend>()

    private val hostConnectionId = ConnectionStore.hostConnectionId

    // ##### HostCommunicator #####
    override fun startServer() {
        Logger.info { "Start server" }
        communicationStateRepository.updateState(HostCommunicationState.Opening)
        observeCommunicationEvents()
        commServer.start("0.0.0.0", 8889)
    }

    override fun stopServer() {
        Logger.info { "Stop server" }
        commServer.stop()
        // Subscribed streams are disposed when ServerCommunicationEvent.NoLongerListeningForConnections has been processed
    }

    override fun closeConnectionWithClient(connectionId: ConnectionId) {
        commServer.closeConnectionWithClient(connectionId)
    }

    override fun sendMessage(envelopeToSend: EnvelopeToSend) {
        when (val recipient = envelopeToSend.recipient) {
            is Recipient.Single -> sendMessageToSingleRecipient(recipient, envelopeToSend)
            Recipient.All -> sendMessageToAllGuests(envelopeToSend)
        }
    }

    override fun sendMessageToHost(message: Message) {
        Logger.info { "Send message to host: $message" }
        hostReceivedMessagesRelay.accept(ServerEvent.EnvelopeReceived(hostConnectionId, message))
    }

    // ##### ComputerCommunicator #####
    override fun sendMessageToHostFromComputerPlayer(envelope: ServerEvent.EnvelopeReceived) {
        hostReceivedMessagesRelay.accept(envelope)
    }

    override fun sendCommunicationEventFromComputerPlayer(event: ServerEvent.CommunicationEvent) {
        hostCommunicationEventsRelay.accept(event)
    }

    override fun observeMessagesForComputerPlayers(): Observable<EnvelopeToSend> {
        return messagesToComputerPlayersRelay
    }

    // ##### private stuff #####
    private fun observeCommunicationEvents() {
        disposableManager.add(
            Observable.merge(
                listOf(
                    commServer.observeEvents(),
                    hostCommunicationEventsRelay,
                    hostReceivedMessagesRelay
                )
            )
                .observeOn(schedulerFactory.single())
                .flatMapCompletable { event ->
                    Completable.merge(
                        listOf(
                            dispatchEvent(event),
                            forwardMessageToGuestsIfNeeded(event)
                        )
                    )
                }
                .subscribe(
                    { Logger.debug { "Communication events stream completed" } },
                    { error -> Logger.error(error) { "Error while observing communication events" } }
                )
        )
    }

    private fun dispatchEvent(event: ServerEvent): Completable {
        return communicationEventDispatcher.dispatch(event)
            .doFinally {
                if (
                    event is ServerEvent.CommunicationEvent.NoLongerListeningForConnections ||
                    event is ServerEvent.CommunicationEvent.ErrorListeningForConnections
                ) {
                    disposableManager.disposeAndReset()
                }
            }
    }

    private fun sendMessageToAllGuests(envelopeToSend: EnvelopeToSend) {
        Logger.info { "Send message to all guests: ${envelopeToSend.message}" }
        commServer.sendMessage(envelopeToSend.message, envelopeToSend.recipient)
        messagesToComputerPlayersRelay.accept(envelopeToSend)
    }

    private fun sendMessageToSingleRecipient(recipient: Recipient.Single, envelopeToSend: EnvelopeToSend) {
        when (recipient.id.value) {
            hostConnectionId.value -> {
                Logger.info { "Send message to host (${envelopeToSend.recipient}): ${envelopeToSend.message}" }
                sendMessageToHost(envelopeToSend.message)
            }
            in ConnectionStore.computerConnectionIdRange -> {
                Logger.info { "Send message to computer guest (${envelopeToSend.recipient}): ${envelopeToSend.message}" }
                messagesToComputerPlayersRelay.accept(envelopeToSend)
            }
            else -> {
                Logger.info { "Send message to human guest (${envelopeToSend.recipient}): ${envelopeToSend.message}" }
                commServer.sendMessage(envelopeToSend.message, envelopeToSend.recipient)
            }
        }
    }

    private fun forwardMessageToGuestsIfNeeded(event: ServerEvent): Completable {
        return when (event) {
            is ServerEvent.EnvelopeReceived ->
                Completable.fromAction {
                    when (event.message) {
                        is Message.GameStateUpdatedMessage -> {
                            commServer.sendMessage(event.message, Recipient.All)
                            messagesToComputerPlayersRelay.accept(EnvelopeToSend(Recipient.All, event.message))
                        }
                        else -> Completable.complete()
                    }
                }
            else -> Completable.complete()
        }
    }
}
