package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import timber.log.Timber
import javax.inject.Inject

class WaitingRoomGuestViewModel @Inject constructor(
    private val facade: WaitingRoomGuestFacade,
    disposableManager: ch.qscqlmpa.dwitchcommonutil.DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<WaitingRoomGuestCommand>()

    fun localPlayerReadyStateInfo(): LiveData<UiCheckboxModel> {
        return LiveDataReactiveStreams.fromPublisher(
            Flowable.combineLatest(
                facade.observeLocalPlayerReadyState().toFlowable(BackpressureStrategy.LATEST),
                currentCommunicationState(),
                { playerReady, connectionState ->
                    when (connectionState) {
                        GuestCommunicationState.Connected -> UiCheckboxModel(enabled = true, checked = playerReady)
                        GuestCommunicationState.Disconnected,
                        GuestCommunicationState.Error -> UiCheckboxModel(enabled = false, checked = false)
                    }
                })
        )
    }

    fun updateReadyState(ready: Boolean) {
        disposableManager.add(
            facade.updateReadyState(ready)
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe()
        )
    }

    fun commands(): LiveData<WaitingRoomGuestCommand> {
        val liveDataMerger = MediatorLiveData<WaitingRoomGuestCommand>()
        liveDataMerger.addSource(gameEventLiveData()) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun acknowledgeGameCanceledEvent() {
        commands.value = WaitingRoomGuestCommand.NavigateToHomeScreen
    }

    fun leaveGame() {
        disposableManager.add(
            facade.leaveGame()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    {
                        Timber.i("Left game successfully")
                        commands.value = WaitingRoomGuestCommand.NavigateToHomeScreen
                    },
                    { error -> Timber.e(error, "Error while leaving game") }
                )
        )
    }

    private fun currentCommunicationState(): Flowable<GuestCommunicationState> {
        return facade.observeCommunicationState()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .doOnError { error -> Timber.e(error, "Error while observing communication state.") }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    private fun gameEventLiveData(): LiveData<WaitingRoomGuestCommand> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observeEvents()
                .observeOn(schedulerFactory.ui())
                .map(::getCommandForGameEvent)
                .doOnError { error -> Timber.e(error, "Error while observing game events.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private fun getCommandForGameEvent(event: GuestGameEvent): WaitingRoomGuestCommand {
        return when (event) {
            GuestGameEvent.GameCanceled -> WaitingRoomGuestCommand.NotifyUserGameCanceled
            GuestGameEvent.GameLaunched -> WaitingRoomGuestCommand.NavigateToGameRoomScreen
            GuestGameEvent.GameOver -> throw IllegalStateException("Event '$event' is not supposed to occur in WaitingRoom.")
        }
    }
}
