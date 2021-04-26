package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

//TODO: Write tests
internal class GameRoomGuestViewModel @Inject constructor(
    private val facade: GameRoomGuestFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _commands = MutableLiveData<GameRoomGuestCommand>()
    private val _gameOver = MutableLiveData<Boolean>()
    val commands get(): LiveData<GameRoomGuestCommand> = _commands
    val gameOver get(): LiveData<Boolean> = _gameOver

    fun acknowledgeGameOver() {
        _gameOver.value = false
        _commands.value = GameRoomGuestCommand.NavigateToHomeScreen
    }

    fun leaveGame() {
        disposableManager.add(facade.leaveGame()
            .observeOn(uiScheduler)
            .subscribe(
                { _commands.value = GameRoomGuestCommand.NavigateToHomeScreen },
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
            facade.observeEvents()
                .observeOn(uiScheduler)
                .doOnNext { event -> Logger.debug("Game event received: $event") }
                .filter { event -> event is GuestGameEvent.GameOver }
                .doOnError { error -> Logger.error(error) { "Error while observing game events." } }
                .subscribe { _gameOver.value = true }
        )
    }
}
