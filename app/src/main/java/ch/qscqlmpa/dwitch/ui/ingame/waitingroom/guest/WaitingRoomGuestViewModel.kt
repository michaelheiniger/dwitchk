package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.common.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class WaitingRoomGuestViewModel @Inject constructor(
    private val facade: WaitingRoomGuestFacade,
    private val gameAdvertisingFacade: GameAdvertisingFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _notifications = mutableStateOf<WaitingRoomGuestNotification>(WaitingRoomGuestNotification.None)
    private val _ready = mutableStateOf(UiCheckboxModel(enabled = false, checked = false))

    val notifications get(): State<WaitingRoomGuestNotification> = _notifications
    val ready get(): State<UiCheckboxModel> = _ready

    fun updateReadyState(ready: Boolean) {
        idlingResource.increment("Click on ready")
        disposableManager.add(
            facade.updateReadyState(ready)
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.info { "Update ready state successfully" } },
                    { error -> Logger.error(error) { "Error while leaving game" } }
                )
        )
    }

    fun leaveGame() {
        disposableManager.add(
            facade.leaveGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Left game successfully" }
                        goToHomeScreen()
                    },
                    { error -> Logger.error(error) { "Error while leaving game" } }
                )
        )
    }

    fun acknowledgeGameCanceled() {
        goToHomeScreen()
    }

    fun acknowledgeKickOffGame() {
        goToHomeScreen()
    }

    override fun onStart() {
        super.onStart()
        gameAdvertisingFacade.startListeningForAdvertisedGames()
        localPlayerReadyState()
        observeGameEvents()
    }

    private fun goToHomeScreen() {
        navigationBridge.navigate(Destination.HomeScreens.Home)
    }

    private fun localPlayerReadyState() {
        disposableManager.add(
            Observable.combineLatest(
                facade.observeLocalPlayerReadyState(),
                currentCommunicationState()
            ) { playerReady, connectionState ->
                when (connectionState) {
                    GuestCommunicationState.Connected -> UiCheckboxModel(enabled = true, checked = playerReady)
                    GuestCommunicationState.Connecting,
                    GuestCommunicationState.Disconnected,
                    GuestCommunicationState.Error -> UiCheckboxModel(enabled = false, checked = false)
                }
            }
                .doOnError { error -> Logger.error(error) { "Error while observing local player state." } }
                .observeOn(uiScheduler)
                .subscribe { value -> _ready.value = value }
        )
    }

    private fun currentCommunicationState(): Observable<GuestCommunicationState> {
        return facade.observeCommunicationState()
            .doOnError { error -> Logger.error(error) { "Error while observing communication state." } }
    }

    private fun observeGameEvents() {
        disposableManager.add(
            facade.observeGameEvents()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing game events." } }
                .subscribe { event ->
                    when (event) {
                        GuestGameEvent.GameCanceled -> _notifications.value = WaitingRoomGuestNotification.NotifyGameCanceled
                        GuestGameEvent.KickedOffGame ->
                            _notifications.value = WaitingRoomGuestNotification.NotifyPlayerKickedOffGame
                        GuestGameEvent.GameLaunched -> navigationBridge.navigate(Destination.GameScreens.GameRoomGuest)
                        GuestGameEvent.GameOver -> throw IllegalStateException("Event '$event' is not supposed to occur in WaitingRoom.")
                    }
                }
        )
    }
}

sealed class WaitingRoomGuestNotification {
    object None : WaitingRoomGuestNotification()
    object NotifyGameCanceled : WaitingRoomGuestNotification()
    object NotifyPlayerKickedOffGame : WaitingRoomGuestNotification()
}
