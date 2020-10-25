package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber

internal class GuestCommunicatorImpl
constructor(private val commClient: CommClient,
            private val messageDispatcher: MessageDispatcher,
            private val communicationEventDispatcher: GuestCommunicationEventDispatcher,
            private val schedulerFactory: SchedulerFactory
) : GuestCommunicator {

    private val disposableManager = DisposableManager()

    // Keeps the last state
    private val communicationStateRelay = BehaviorRelay.create<GuestCommunicationState>()

    private var isListening: Boolean = false

    override fun connect() {
        if (isListening) {
            return
        }
        isListening = true

        observeReceivedMessages()
        observeCommunicationEvents()

        commClient.start()
    }

    override fun closeConnection() {
        commClient.stop()
        disposableManager.disposeAndReset()
    }

    override fun observeCommunicationState(): Observable<GuestCommunicationState> {
        return communicationStateRelay
    }

    override fun sendMessage(envelopeToSend: EnvelopeToSend): Completable {
        return commClient.sendMessage(envelopeToSend.message)
    }

    private fun observeReceivedMessages() {
        disposableManager.add(commClient.observeReceivedMessages()
                .subscribeOn(schedulerFactory.io())
                .flatMapCompletable(messageDispatcher::dispatch)
                .subscribe(
                        { Timber.d("Dispatch Completed !") },
                        { error -> Timber.e(error, "Error while observing received messages") }
                )
        )
    }

    private fun observeCommunicationEvents() {
        disposableManager.add(commClient.observeCommunicationEvents()
                .subscribeOn(schedulerFactory.io())
                .flatMapSingle(this::dispatchCommunicationEvent)
                .subscribe(
                        { event -> emitGuestCommunicationStateIfAny(event) },
                        { error -> Timber.e(error, "Error while observing communication events") }
                )
        )
    }

    private fun dispatchCommunicationEvent(event: ClientCommunicationEvent): Single<ClientCommunicationEvent> {
        return communicationEventDispatcher.dispatch(event)
                .andThen(Single.just(event))
    }

    private fun emitGuestCommunicationStateIfAny(event: ClientCommunicationEvent) {
        when (event) {
            ConnectedToHost -> communicationStateRelay.accept(GuestCommunicationState.CONNECTED)
            DisconnectedFromHost -> communicationStateRelay.accept(GuestCommunicationState.DISCONNECTED)
            is ConnectionError -> TODO()
        }
    }
}
