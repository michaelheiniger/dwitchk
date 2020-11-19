package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ongoinggame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import timber.log.Timber
import javax.inject.Inject

internal class GameRoomHostViewModel @Inject
constructor(
    private val facade: GameRoomHostFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<GameRoomHostCommand>()

    fun commands(): LiveData<GameRoomHostCommand> {
        return commands
    }

    fun endGame() {
        disposableManager.add(facade.endGame()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .subscribe(
                {
                    Timber.d("Game ended successfully.")
                    commands.value = GameRoomHostCommand.NavigateToHomeScreen
                },
                { error -> Timber.e(error, "Error while ending game.") }
            ))
    }
}