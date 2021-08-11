package ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomGuestFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

//TODO: Write tests
internal class GameRoomGuestViewModel @Inject constructor(
    private val facade: GameRoomGuestFacade,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _navigationCommand = mutableStateOf<GameRoomGuestDestination>(GameRoomGuestDestination.CurrentScreen)
    private val _gameOver = mutableStateOf(false)
    val navigation get(): State<GameRoomGuestDestination> = _navigationCommand
    val gameOver get(): State<Boolean> = _gameOver

    fun acknowledgeGameOver() {
        _gameOver.value = false
        _navigationCommand.value = GameRoomGuestDestination.NavigateToHomeScreen
    }

    fun leaveGame() {
        disposableManager.add(
            facade.leaveGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Left game successfully" }
                        _navigationCommand.value = GameRoomGuestDestination.NavigateToHomeScreen
                    },
                    { error -> Logger.error(error) { "Error while leaving the game." } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        observeGameEvent()
    }

    private fun observeGameEvent() {
        disposableManager.add(
            facade.observeGameEvents()
                .observeOn(uiScheduler)
                .doOnNext { event -> Logger.debug("Game event received: $event") }
                .filter { event -> event is GuestGameEvent.GameOver }
                .doOnNext { event -> idlingResource.decrement("Game event received ($event)") }
                .subscribe(
                    { _gameOver.value = true },
                    { error -> Logger.error(error) { "Error while observing game events." } }
                )
        )
    }
}
