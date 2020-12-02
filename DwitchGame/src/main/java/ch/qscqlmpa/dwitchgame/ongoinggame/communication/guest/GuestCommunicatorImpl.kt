package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber

internal class GuestCommunicatorImpl constructor(
    private val commClient: CommClient,
    private val messageDispatcher: MessageDispatcher,
    private val communicationEventDispatcher: GuestCommunicationEventDispatcher,
    private val communicationStateRepository: GuestCommunicationStateRepository,
    private val schedulerFactory: SchedulerFactory
) : GuestCommunicator {

    private val disposableManager = ch.qscqlmpa.dwitchcommonutil.DisposableManager()

    override fun connect() {
        observeCommunicationEvents()
        observeReceivedMessages()
        commClient.start()
    }

    override fun closeConnection() {
        disposableManager.disposeAndReset()
        commClient.stop()
    }

    override fun sendMessage(envelopeToSend: EnvelopeToSend): Completable {
        return commClient.sendMessage(envelopeToSend.message)
    }

    override fun observeCommunicationState(): Observable<GuestCommunicationState> {
        return communicationStateRepository.observeEvents()
    }

    override fun observePlayerConnectionState(): Observable<PlayerConnectionState> {
        return communicationStateRepository.observeEvents().map { state ->
            when (state) {
                GuestCommunicationState.Connected -> PlayerConnectionState.CONNECTED
                GuestCommunicationState.Disconnected,
                GuestCommunicationState.Error -> PlayerConnectionState.DISCONNECTED
            }
        }
    }

    private fun observeCommunicationEvents() {
        disposableManager.add(commClient.observeCommunicationEvents()
            .subscribeOn(schedulerFactory.io())
            .flatMapCompletable(communicationEventDispatcher::dispatch)
            .subscribe(
                { Timber.d("Communication events stream completed") },
                { error -> Timber.e(error, "Error while observing communication events") }
            )
        )
    }

    private fun observeReceivedMessages() {
        disposableManager.add(commClient.observeReceivedMessages()
            .subscribeOn(schedulerFactory.io())
            .flatMapCompletable(messageDispatcher::dispatch)
            .subscribe(
                { Timber.d("Received messages stream completed !") },
                { error -> Timber.e(error, "Error while observing received messages") }
            )
        )
    }
}
