package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger

internal class HostCommunicatorImpl constructor(
    private val inGameStore: InGameStore,
    private val commServer: CommServer,
    private val messageDispatcher: MessageDispatcher,
    private val communicationEventDispatcher: HostCommunicationEventDispatcher,
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val connectionStore: ConnectionStore,
    private val schedulerFactory: SchedulerFactory,
    private val idlingResource: DwitchIdlingResource
) : HostCommunicator {

    private val disposableManager = DisposableManager()

    private val receivedMessageRelay = PublishRelay.create<EnvelopeReceived>()

    override fun startServer() {
        Logger.trace { "Start server" }
        communicationStateRepository.updateState(HostCommunicationState.Opening)
        observeCommunicationEvents()
        observeReceivedMessages()
        commServer.start()
    }

    override fun stopServer() {
        Logger.info { "Close all connections" }
        commServer.stop()
        disposableManager.disposeAndReset()
    }

    override fun sendMessage(envelopeToSend: EnvelopeToSend) {
        when (val recipient = envelopeToSend.recipient) {
            is Recipient.Single -> sendMessageToSingleRecipient(recipient, envelopeToSend)
            Recipient.All -> sendMessageToAllGuests(envelopeToSend)
        }
    }

    override fun sendMessageToHost(message: Message) {
        Logger.info { "Send message to host: $message" }
        idlingResource.increment()
        receivedMessageRelay.accept(EnvelopeReceived(hostConnectionId(), message))
    }

    override fun closeConnectionWithClient(connectionId: ConnectionId) {
        commServer.closeConnectionWithClient(connectionId)
    }

    override fun observePlayerConnectionState(): Observable<PlayerConnectionState> {
        return communicationStateRepository.currentState().map { state ->
            when (state) {
                HostCommunicationState.Open -> PlayerConnectionState.CONNECTED
                HostCommunicationState.Opening,
                HostCommunicationState.Closed,
                HostCommunicationState.Error -> PlayerConnectionState.DISCONNECTED
            }
        }
    }

    private fun observeCommunicationEvents() {
        disposableManager.add(
            commServer.observeCommunicationEvents()
                .flatMapCompletable { event ->
                    communicationEventDispatcher.dispatch(event)
                        .subscribeOn(schedulerFactory.single())
                        .doOnComplete { idlingResource.decrement() }
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
                    receivedMessageRelay
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
            .doOnComplete { idlingResource.decrement() }

    private fun hostConnectionId(): ConnectionId {
        val hostDwitchId = inGameStore.getLocalPlayerDwitchId()
        return connectionStore.getConnectionId(hostDwitchId)
            ?: throw IllegalStateException("The host has no connection ID mapped to its in-game ID: $hostDwitchId.")
    }

    private fun sendMessageToAllGuests(envelopeToSend: EnvelopeToSend) {
        Logger.info { "Send message to all guests: ${envelopeToSend.message}" }
        commServer.sendMessage(envelopeToSend.message, envelopeToSend.recipient)
        // Should messages with recipient "All" be also sent to the host ? It doesn't seem needed since the message
        // is sent by the host.
    }

    private fun sendMessageToSingleRecipient(recipient: Recipient.Single, envelopeToSend: EnvelopeToSend) {
        if (hostConnectionId() == recipient.id) {
            sendMessageToHost(envelopeToSend.message)
        } else {
            Logger.info { "Send message to guest (${envelopeToSend.recipient}): ${envelopeToSend.message}" }
            commServer.sendMessage(envelopeToSend.message, envelopeToSend.recipient)
        }
    }

    private fun forwardMessageToGuestsIfNeeded(envelopeReceived: EnvelopeReceived): Completable {
        return Completable.fromAction {
            when (envelopeReceived.message) {
                is Message.GameStateUpdatedMessage -> commServer.sendMessage(envelopeReceived.message, Recipient.All)
                else -> Completable.complete()
            }
        }.subscribeOn(schedulerFactory.io())
    }
}
