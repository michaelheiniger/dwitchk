package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.AddressType
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber

internal class HostCommunicatorImpl
constructor(private val commServer: CommServer,
            private val messageDispatcher: MessageDispatcher,
            private val communicationEventDispatcher: HostCommunicationEventDispatcher,
            private val schedulerFactory: SchedulerFactory,
            private val localConnectionIdStore: LocalConnectionIdStore
) : HostCommunicator {

    private val disposableManager = DisposableManager()

    // Keeps the last state
    private val communicationStateRelay = BehaviorRelay.create<HostCommunicationState>()

    private var isConnected: Boolean = false

    override fun listenForConnections() {
        if (isConnected) {
            return
        }
        isConnected = true

        commServer.start()

        observeReceivedMessages()
        observeCommunicationEvents()
    }

    override fun closeAllConnections() {
        Timber.d("Close all connections")
        commServer.stop()
        disposableManager.disposeAndReset()
    }

    override fun observeCommunicationState(): Observable<HostCommunicationState> {
        return communicationStateRelay
    }

    override fun sendMessage(envelopeToSend: EnvelopeToSend): Completable {
        val address = getRecipientAddress(envelopeToSend.recipient)
        return commServer.sendMessage(envelopeToSend.message, address)
    }

    override fun closeConnectionWithClient(localConnectionId: LocalConnectionId) {
        commServer.closeConnectionWithClient(localConnectionId)
    }

    private fun observeReceivedMessages() {
        disposableManager.add(commServer.observeReceivedMessages()
                .subscribeOn(schedulerFactory.io())
                .flatMapCompletable(messageDispatcher::dispatch)
                .subscribe(
                        { Timber.d("Dispatch Completed !") },
                        { error -> Timber.e(error, "Error while observing received messages") }
                )
        )
    }

    private fun observeCommunicationEvents() {
        disposableManager.add(commServer.observeCommunicationEvents()
                .subscribeOn(schedulerFactory.io())
                .flatMapSingle(this::dispatchCommunicationEvent)
                .observeOn(schedulerFactory.ui())
                .subscribe(
                        { event -> emitHostCommunicationStateIfAny(event) },
                        { error -> Timber.e(error, "Error while observing communication events") }
                )
        )
    }

    private fun dispatchCommunicationEvent(event: ServerCommunicationEvent): Single<ServerCommunicationEvent> {
        return communicationEventDispatcher.dispatch(event)
                .andThen(Single.just(event))
    }

    private fun emitHostCommunicationStateIfAny(event: ServerCommunicationEvent) {
        when (event) {
            is ClientConnected -> {/*Nothing to do*/
            }
            is ClientDisconnected -> {/*Nothing to do*/
            }
            is ListeningForConnections -> communicationStateRelay.accept(HostCommunicationState.LISTENING_FOR_GUESTS)
            is NotListeningForConnections -> communicationStateRelay.accept(HostCommunicationState.NOT_LISTENING_FOR_GUESTS)
        }
    }

    private fun getRecipientAddress(recipient: RecipientType): AddressType {
        return when (recipient) {
            is RecipientType.Single -> AddressType.Unicast(localConnectionIdStore.getAddress(recipient.localId)!!)
            RecipientType.All -> AddressType.Broadcast
        }
    }
}
