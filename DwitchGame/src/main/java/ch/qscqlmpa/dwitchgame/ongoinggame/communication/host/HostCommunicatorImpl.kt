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

internal class HostCommunicatorImpl
constructor(
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

    override fun listenForConnections() {
        Timber.i("Listening for connections...")
        observeCommunicationEvents()
        observeReceivedMessages()
        commServer.start()
    }

    override fun closeAllConnections() {
        Timber.i("Close all connections")
        disposableManager.disposeAndReset()
        commServer.stop()
    }

    //TODO: clean-up and test
    override fun sendMessage(envelopeToSend: EnvelopeToSend): Completable {
        return when (val recipient = envelopeToSend.recipient) {
            is Recipient.SingleGuest -> {
                hostConnectionId()
                    .flatMapCompletable { hostConnectionId ->
                        if (hostConnectionId == recipient.id) {
                            Timber.i("Send message to host: ${envelopeToSend.message}")
                            Completable.fromAction {
                                receivedMessageRelay.accept(EnvelopeReceived(hostConnectionId, envelopeToSend.message))
                            }
                        } else {
                            Timber.i("Send envelope to guest: $envelopeToSend")
                            commServer.sendMessage(envelopeToSend.message, envelopeToSend.recipient)
                        }
                    }
            }
            Recipient.AllGuests -> {
                Timber.i("Send envelope to all guests: ${envelopeToSend.message}")
                commServer.sendMessage(envelopeToSend.message, envelopeToSend.recipient)
            }
        }
    }

    private fun hostConnectionId(): Single<ConnectionId> {
        return Single.fromCallable {
            val hostInGameId = inGameStore.getLocalPlayerInGameId()
            connectionStore.getConnectionId(hostInGameId)
                ?: throw IllegalStateException("The host has no connection ID.")
        }
    }

    override fun sendMessageToHost(message: Message): Completable {
        Timber.i("Send message to host: $message")
        return Completable.fromAction {
            val hostInGameId = inGameStore.getLocalPlayerInGameId()
            val connectionId = connectionStore.getConnectionId(hostInGameId)
                ?: throw IllegalStateException("The host has no connection ID.")
            receivedMessageRelay.accept(EnvelopeReceived(connectionId, message))
        }
    }

    override fun closeConnectionWithClient(connectionId: ConnectionId) {
        commServer.closeConnectionWithClient(connectionId)
    }

    override fun observeCommunicationState(): Observable<HostCommunicationState> {
        return communicationStateRepository.observeEvents()
    }

    override fun observePlayerConnectionState(): Observable<PlayerConnectionState> {
        return communicationStateRepository.observeEvents().map { state ->
            when (state) {
                HostCommunicationState.Open -> PlayerConnectionState.CONNECTED
                HostCommunicationState.Closed,
                HostCommunicationState.Error -> PlayerConnectionState.DISCONNECTED
            }
        }
    }

    private fun observeCommunicationEvents() {
        disposableManager.add(commServer.observeCommunicationEvents()
            .subscribeOn(schedulerFactory.io())
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
                        dispatchReceivedMessage(envelopeReceived),
                        forwardMessageToGuestsIfNeeded(envelopeReceived)
                    )
                )
            }.subscribe(
                { Timber.d("Received messages stream completed !") },
                { error -> Timber.e(error, "Error while observing received messages") }
            )
        )
    }

    private fun dispatchReceivedMessage(envelopeReceived: EnvelopeReceived) =
        messageDispatcher.dispatch(envelopeReceived)
            .subscribeOn(schedulerFactory.io())

    private fun forwardMessageToGuestsIfNeeded(envelopeReceived: EnvelopeReceived): Completable {
        return when (envelopeReceived.message) {
            is Message.GameStateUpdatedMessage -> commServer.sendMessage(envelopeReceived.message, Recipient.AllGuests)
            else -> Completable.complete()
        }.subscribeOn(schedulerFactory.io())
    }
}
