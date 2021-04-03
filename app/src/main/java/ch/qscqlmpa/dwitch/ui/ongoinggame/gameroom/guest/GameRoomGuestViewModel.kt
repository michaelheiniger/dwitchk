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

    private val _commands = MutableLiveData<GameRoomGuestCommand>()

    val commands get(): LiveData<GameRoomGuestCommand> = _commands

    fun acknowledgeGameOverEvent() {
        _commands.value = GameRoomGuestCommand.NavigateToHomeScreen
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
                .map(::mapCommandToGameEvent)
                .doOnError { error -> Logger.error(error) { "Error while observing game events." } }
                .subscribe { command -> _commands.value = command }
        )
    }

    private fun mapCommandToGameEvent(event: GuestGameEvent): GameRoomGuestCommand {
        return when (event) {
            GuestGameEvent.GameOver -> GameRoomGuestCommand.ShowGameOverInfo
            else -> throw IllegalStateException("Event '$event' is not supposed to occur in GameRoom.")
        }
    }
}
