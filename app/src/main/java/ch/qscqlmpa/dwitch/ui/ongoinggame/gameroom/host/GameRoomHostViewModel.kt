package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomHostFacade
import io.reactivex.rxjava3.core.Scheduler
import mu.KLogging
import javax.inject.Inject

internal class GameRoomHostViewModel @Inject
constructor(
    private val facade: GameRoomHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val commands = MutableLiveData<GameRoomHostCommand>()

    fun commands(): LiveData<GameRoomHostCommand> {
        return commands
    }

    fun endGame() {
        disposableManager.add(
            facade.endGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        logger.debug { "Game ended successfully." }
                        commands.value = GameRoomHostCommand.NavigateToHomeScreen
                    },
                    { error -> logger.error(error) { "Error while ending game." } }
                )
        )
    }

    companion object : KLogging()
}
