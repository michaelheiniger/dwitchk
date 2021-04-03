package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomHostFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GameRoomHostViewModel @Inject
constructor(
    private val facade: GameRoomHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _commands = MutableLiveData<GameRoomHostCommand>()

    val commands get(): LiveData<GameRoomHostCommand> = _commands

    fun endGame() {
        disposableManager.add(
            facade.endGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.debug { "Game ended successfully." }
                        _commands.value = GameRoomHostCommand.NavigateToHomeScreen
                    },
                    { error -> Logger.error(error) { "Error while ending game." } }
                )
        )
    }
}
