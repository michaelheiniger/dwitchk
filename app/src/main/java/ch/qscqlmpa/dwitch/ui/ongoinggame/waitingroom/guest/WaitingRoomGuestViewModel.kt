package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomGuestFacade
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class WaitingRoomGuestViewModel @Inject constructor(
    private val facade: WaitingRoomGuestFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _navigation = MutableLiveData<WaitingRoomGuestDestination>()
    private val _ready = MutableLiveData(UiCheckboxModel(enabled = false, checked = false))

    val navigation get(): LiveData<WaitingRoomGuestDestination> = _navigation
    val ready get(): LiveData<UiCheckboxModel> = _ready

    override fun onStart() {
        super.onStart()
        localPlayerReadyState()
        gameEventLiveData()
    }

    fun updateReadyState(ready: Boolean) {
        disposableManager.add(
            facade.updateReadyState(ready)
                .observeOn(uiScheduler)
                .subscribe()
        )
    }

    fun acknowledgeGameCanceledEvent() {
        _navigation.value = WaitingRoomGuestDestination.NavigateToHomeScreen
    }

    fun leaveGame() {
        disposableManager.add(
            facade.leaveGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Left game successfully" }
                        _navigation.value = WaitingRoomGuestDestination.NavigateToHomeScreen
                    },
                    { error -> Logger.error(error) { "Error while leaving game" } }
                )
        )
    }

    private fun localPlayerReadyState() {
        disposableManager.add(
            Observable.combineLatest(
                facade.observeLocalPlayerReadyState(),
                currentCommunicationState(),
                { playerReady, connectionState ->
                    when (connectionState) {
                        GuestCommunicationState.Connected -> UiCheckboxModel(enabled = true, checked = playerReady)
                        GuestCommunicationState.Connecting,
                        GuestCommunicationState.Disconnected,
                        GuestCommunicationState.Error -> UiCheckboxModel(enabled = false, checked = false)
                    }
                }
            )
                .doOnError { error -> Logger.error(error) { "Error while observing local player state." } }
                .observeOn(uiScheduler)
                .subscribe { value -> _ready.value = value }
        )
    }

    private fun currentCommunicationState(): Observable<GuestCommunicationState> {
        return facade.observeCommunicationState()
            .doOnError { error -> Logger.error(error) { "Error while observing communication state." } }
    }

    private fun gameEventLiveData() {
        disposableManager.add(
            facade.observeGameEvents()
                .observeOn(uiScheduler)
                .map(::getCommandForGameEvent)
                .doOnError { error -> Logger.error(error) { "Error while observing game events." } }
                .subscribe { command -> _navigation.value = command }
        )
    }

    private fun getCommandForGameEvent(event: GuestGameEvent): WaitingRoomGuestDestination {
        return when (event) {
            GuestGameEvent.GameCanceled -> WaitingRoomGuestDestination.NotifyUserGameCanceled
            GuestGameEvent.GameLaunched -> WaitingRoomGuestDestination.NavigateToGameRoomScreen
            GuestGameEvent.GameOver -> throw IllegalStateException("Event '$event' is not supposed to occur in WaitingRoom.")
        }
    }
}
