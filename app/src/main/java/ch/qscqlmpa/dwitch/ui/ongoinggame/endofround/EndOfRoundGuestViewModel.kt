package ch.qscqlmpa.dwitch.ui.ongoinggame.endofround

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class EndOfRoundGuestViewModel @Inject constructor(
    private val facade: GameDashboardFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _commands = MutableLiveData<EndOfRoundGuestCommand>()
    val commands get(): LiveData<EndOfRoundGuestCommand> = _commands

    fun leaveGame() {
        disposableManager.add(
            facade.startNewRound() //TODO: leave game
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.debug { "Start new round successfully." }
                        _commands.value = EndOfRoundGuestCommand.NavigateHome
                    },
                    { error -> Logger.error(error) { "Error while starting new round." } }
                )
        )
    }
}
