package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameGuestDestination
import ch.qscqlmpa.dwitch.ui.navigation.ScreenNavigator
import ch.qscqlmpa.dwitch.ui.navigation.navOptionsPopUpToInclusive
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@Suppress("LongParameterList")
class WaitingRoomGuestViewModel @Inject constructor(
    private val waitingRoomGuestFacade: WaitingRoomGuestFacade,
    private val guestCommunicationFacade: GuestCommunicationFacade,
    private val inGameGuestFacade: InGameGuestFacade,
    private val screenNavigator: ScreenNavigator,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _notifications = mutableStateOf<WaitingRoomGuestNotification>(WaitingRoomGuestNotification.None)
    private val _ready = mutableStateOf(UiCheckboxModel(enabled = false, checked = false))

    val notifications get(): State<WaitingRoomGuestNotification> = _notifications
    val ready get(): State<UiCheckboxModel> = _ready

    init {
        idlingResource.decrement("Navigated to in-game")
    }

    fun updateReadyState(ready: Boolean) {
        idlingResource.increment("Click on ready")
        disposableManager.add(
            waitingRoomGuestFacade.updateReadyState(ready)
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.info { "Update ready state successfully" } },
                    { error -> Logger.error(error) { "Error while leaving game" } }
                )
        )
    }

    fun leaveGame() {
        disposableManager.add(
            inGameGuestFacade.leaveGame()
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
        localPlayerReadyState()
        observeGameEvents()
    }

    private fun goToHomeScreen() {
        screenNavigator.navigate(
            destination = HomeDestination.Home,
            navOptions = navOptionsPopUpToInclusive(InGameGuestDestination.WaitingRoom)
        )
    }

    private fun localPlayerReadyState() {
        disposableManager.add(
            Observable.combineLatest(
                waitingRoomGuestFacade.observeLocalPlayerReadyState(),
                currentCommunicationState()
            ) { playerReady, connectionState ->
                when (connectionState) {
                    GuestCommunicationState.Connected -> UiCheckboxModel(enabled = true, checked = playerReady)
                    GuestCommunicationState.Connecting,
                    is GuestCommunicationState.Disconnected,
                    is GuestCommunicationState.Error -> UiCheckboxModel(enabled = false, checked = false)
                }
            }
                .observeOn(uiScheduler)
                .subscribe(
                    { value -> _ready.value = value },
                    { error -> Logger.error(error) { "Error while observing local player state." } }
                )
        )
    }

    private fun currentCommunicationState(): Observable<GuestCommunicationState> {
        return guestCommunicationFacade.currentCommunicationState()
            .doOnError { error -> Logger.error(error) { "Error while observing communication state." } }
    }

    private fun observeGameEvents() {
        disposableManager.add(
            inGameGuestFacade.observeGameEvents()
                .observeOn(uiScheduler)
                .subscribe(
                    { event ->
                        when (event) {
                            GuestGameEvent.GameCanceled -> _notifications.value = WaitingRoomGuestNotification.NotifyGameCanceled
                            GuestGameEvent.KickedOffGame ->
                                _notifications.value = WaitingRoomGuestNotification.NotifyPlayerKickedOffGame
                            GuestGameEvent.GameLaunched -> screenNavigator.navigate(
                                destination = InGameGuestDestination.GameRoom,
                                navOptions = navOptionsPopUpToInclusive(InGameGuestDestination.WaitingRoom)
                            )
                            GuestGameEvent.GameOver -> throw IllegalStateException("Event '$event' is not supposed to occur in WaitingRoom.")
                        }
                    },
                    { error -> Logger.error(error) { "Error while observing game events." } }
                )
        )
    }
}

sealed class WaitingRoomGuestNotification {
    object None : WaitingRoomGuestNotification()
    object NotifyGameCanceled : WaitingRoomGuestNotification()
    object NotifyPlayerKickedOffGame : WaitingRoomGuestNotification()
}
