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
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

internal class HostCommunicatorImpl constructor(
    private val inGameStore: InGameStore,
    private val commServer: CommServer,
    private val messageDispatcher: MessageDispatcher,
    private val communicationEventDispatcher: HostCommunicationEventDispatcher,
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val connectionStore: ConnectionStore,
    private val schedulerFactory: SchedulerFactory
) : HostCommunicator {

    private val disposableManager = DisposableManager()

    private val receivedMessageRelay = PublishRelay.create<EnvelopeReceived>()

    override fun startServer() {
        Timber.v("Start server")
        observeCommunicationEvents()
        observeReceivedMessages()
        commServer.start()
    }

    override fun stopServer() {
        Timber.i("Close all connections")
        commServer.stop()
        disposableManager.disposeAndReset()
    }

    override fun sendMessage(envelopeToSend: EnvelopeToSend): Completable {
        return when (val recipient = envelopeToSend.recipient) {
            is Recipient.Single -> sendMessageToSingleRecipient(recipient, envelopeToSend)
            Recipient.All -> sendMessageToAllGuests(envelopeToSend)
        }
    }

    override fun sendMessageToHost(message: Message): Completable {
        Timber.i("Send message to host: $message")
        return hostConnectionId()
            .subscribeOn(schedulerFactory.io())
            .doOnSuccess { hostConnectionId -> receivedMessageRelay.accept(EnvelopeReceived(hostConnectionId, message)) }
            .ignoreElement()
    }

    override fun closeConnectionWithClient(connectionId: ConnectionId) {
        commServer.closeConnectionWithClient(connectionId)
    }

    override fun observePlayerConnectionState(): Observable<PlayerConnectionState> {
        return communicationStateRepository.currentState().map { state ->
            when (state) {
                HostCommunicationState.Open -> PlayerConnectionState.CONNECTED
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
                        .subscribeOn(schedulerFactory.io())
                }
                .subscribe(
                    { Timber.d("Communication events stream completed") },
                    { error -> Timber.e(error, "Error while observing communication events") }
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
                        messageDispatcher.dispatch(envelopeReceived)
                            .subscribeOn(schedulerFactory.io()),
                        forwardMessageToGuestsIfNeeded(envelopeReceived)
                    )
                )
            }.subscribe(
                { Timber.d("Received messages stream completed !") },
                { error -> Timber.e(error, "Error while observing received messages") }
            )
        )
    }

    private fun hostConnectionId(): Single<ConnectionId> {
        return Single.fromCallable {
            val hostDwitchId = inGameStore.getLocalPlayerDwitchId()
            connectionStore.getConnectionId(hostDwitchId)
                ?: throw IllegalStateException("The host has no connection ID mapped to its in-game ID: $hostDwitchId.")
        }
    }

    private fun sendMessageToAllGuests(envelopeToSend: EnvelopeToSend): Completable {
        Timber.i("Send message to all guests: ${envelopeToSend.message}")
        return commServer.sendMessage(envelopeToSend.message, envelopeToSend.recipient)
        //TODO: Should messages with recipient "All" be also sent to the host ? It doesn't seem needed since the message
        // is sent by the host.
    }

    private fun sendMessageToSingleRecipient(recipient: Recipient.Single, envelopeToSend: EnvelopeToSend): Completable {
        return hostConnectionId()
            .flatMapCompletable { hostConnectionId ->
                if (hostConnectionId == recipient.id) {
                    sendMessageToHost(envelopeToSend.message)
                } else {
                    Timber.i("Send message to guest (${envelopeToSend.recipient}): ${envelopeToSend.message}")
                    commServer.sendMessage(envelopeToSend.message, envelopeToSend.recipient)
                }
            }
    }

    private fun forwardMessageToGuestsIfNeeded(envelopeReceived: EnvelopeReceived): Completable {
        return when (envelopeReceived.message) {
            is Message.GameStateUpdatedMessage -> commServer.sendMessage(envelopeReceived.message, Recipient.All)
                .subscribeOn(schedulerFactory.io())
            else -> Completable.complete()
        }
    }
}
