package ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _navigationCommand = MutableLiveData<GameRoomGuestDestination>()
    private val _gameOver = MutableLiveData<Boolean>()
    val navigation get(): LiveData<GameRoomGuestDestination> = _navigationCommand
    val gameOver get(): LiveData<Boolean> = _gameOver

    fun acknowledgeGameOver() {
        _gameOver.value = false
        _navigationCommand.value = GameRoomGuestDestination.NavigateToHomeScreen
    }

    fun leaveGame() {
        disposableManager.add(facade.leaveGame()
            .observeOn(uiScheduler)
            .subscribe(
                { _navigationCommand.value = GameRoomGuestDestination.NavigateToHomeScreen },
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
                .doOnError { error -> Logger.error(error) { "Error while observing game events." } }
                .subscribe { _gameOver.value = true }
        )
    }
}
