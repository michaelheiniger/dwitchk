package ch.qscqlmpa.dwitch.ui.ongoinggame.endofround

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class EndOfRoundHostViewModel @Inject constructor(
    private val facade: GameDashboardFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _commands = MutableLiveData<EndOfRoundHostCommand>()
    val commands get(): LiveData<EndOfRoundHostCommand> = _commands

    fun startNewRound() {
        disposableManager.add(
            facade.startNewRound()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.debug { "Start new round successfully." }
                        _commands.value = EndOfRoundHostCommand.NavigateToGameRoom
                    },
                    { error -> Logger.error(error) { "Error while starting new round." } }
                )
        )
    }

    fun endGame() {
        disposableManager.add(
            facade.startNewRound() //TODO: end game
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.debug { "Start new round successfully." }
                        _commands.value = EndOfRoundHostCommand.NavigateHome
                    },
                    { error -> Logger.error(error) { "Error while starting new round." } }
                )
        )
    }
}
