package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger

internal class HostCommunicatorImpl constructor(
    private val commServer: CommServer,
    private val messageDispatcher: MessageDispatcher,
    private val communicationEventDispatcher: HostCommunicationEventDispatcher,
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val schedulerFactory: SchedulerFactory
) : HostCommunicator, ComputerCommunicator {

    private val disposableManager = DisposableManager()

    private val hostReceivedMessagesRelay = PublishRelay.create<EnvelopeReceived>()
    private val hostCommunicationEventsRelay = PublishRelay.create<ServerCommunicationEvent>()
    private val messagesToComputerPlayersRelay = PublishRelay.create<EnvelopeToSend>()

    private val hostConnectionId = ConnectionStore.hostConnectionId

    // ##### HostCommunicator #####
    override fun startServer() {
        Logger.info { "Start server" }
        communicationStateRepository.updateState(HostCommunicationState.Opening)
        observeCommunicationEvents()
        observeReceivedMessages()
        commServer.start()
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
        hostReceivedMessagesRelay.accept(EnvelopeReceived(hostConnectionId, message))
    }

    // ##### ComputerCommunicator #####
    override fun sendMessageToHostFromComputerPlayer(envelope: EnvelopeReceived) {
        hostReceivedMessagesRelay.accept(envelope)
    }

    override fun sendCommunicationEventFromComputerPlayer(event: ServerCommunicationEvent) {
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
                    commServer.observeCommunicationEvents(),
                    hostCommunicationEventsRelay
                )
            ).flatMapCompletable { event ->
                communicationEventDispatcher.dispatch(event)
                    .subscribeOn(schedulerFactory.single())
                    .doFinally {
                        if (event is ServerCommunicationEvent.NoLongerListeningForConnections ||
                            event is ServerCommunicationEvent.ErrorListeningForConnections
                        ) {
                            disposableManager.disposeAndReset()
                        }
                    }
            }
                .subscribe(
                    { Logger.debug { "Communication events stream completed" } },
                    { error -> Logger.error(error) { "Error while observing communication events" } }
                )
        )
    }

    private fun observeReceivedMessages() {
        disposableManager.add(
            Observable.merge(
                listOf(
                    commServer.observeReceivedMessages(),
                    hostReceivedMessagesRelay
                )
            ).flatMapCompletable { envelopeReceived ->
                Completable.merge(
                    listOf(
                        dispatchReceivedMessage(envelopeReceived),
                        forwardMessageToGuestsIfNeeded(envelopeReceived)
                    )
                )
            }.subscribe(
                { Logger.debug { "Received messages stream completed !" } },
                { error -> Logger.error(error) { "Error while observing received messages" } }
            )
        )
    }

    private fun dispatchReceivedMessage(envelopeReceived: EnvelopeReceived) =
        messageDispatcher.dispatch(envelopeReceived)
            .subscribeOn(schedulerFactory.single())

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

    private fun forwardMessageToGuestsIfNeeded(envelopeReceived: EnvelopeReceived): Completable {
        return Completable.fromAction {
            when (envelopeReceived.message) {
                is Message.GameStateUpdatedMessage -> {
                    commServer.sendMessage(envelopeReceived.message, Recipient.All)
                    messagesToComputerPlayersRelay.accept(EnvelopeToSend(Recipient.All, envelopeReceived.message))
                }
                else -> Completable.complete()
            }
        }.subscribeOn(schedulerFactory.io())
    }
}
