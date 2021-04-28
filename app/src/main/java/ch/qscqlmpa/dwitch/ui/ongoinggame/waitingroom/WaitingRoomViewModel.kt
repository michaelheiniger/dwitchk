package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class WaitingRoomViewModel @Inject constructor(
    private val facade: WaitingRoomFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _showAddComputerPlayer = MutableLiveData<Boolean>()
    private val _players = MutableLiveData<List<PlayerWrUi>>(emptyList())
    val canComputerPlayersBeAdded get(): LiveData<Boolean> = _showAddComputerPlayer
    val players get(): LiveData<List<PlayerWrUi>> = _players

    init {
        loadGame()
    }

    override fun onStart() {
        playersInWaitingRoom()
    }

    private fun loadGame() {
        disposableManager.add(
            facade.isGameANewGame()
                .observeOn(uiScheduler)
                .subscribe { gameIsNew -> _showAddComputerPlayer.value = gameIsNew }
        )
    }

    private fun playersInWaitingRoom() {
        disposableManager.add(
            facade.observePlayers()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing connected players." } }
                .subscribe { players -> _players.value = players }
        )
    }
}
