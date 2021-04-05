package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GameRoomGuestViewModel @Inject constructor(
    private val facade: GameRoomGuestFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _gameOver = MutableLiveData<Boolean>()
    val gameOver get(): LiveData<Boolean> = _gameOver

    fun acknowledgeGameOver() {
        _gameOver.value = false
    }

    override fun onStart() {
        super.onStart()
        observeGameEvent()
    }

    override fun onStop() {
        super.onStop()
        disposableManager.disposeAndReset()
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
