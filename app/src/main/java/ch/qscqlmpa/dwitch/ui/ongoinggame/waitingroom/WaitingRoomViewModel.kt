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

    private val _toolbarTitle = MutableLiveData<String>()
    private val _canComputerPlayersBeAdded = MutableLiveData<Boolean>()
    private val _players = MutableLiveData<List<PlayerWrUi>>(emptyList())

    val toolbarTitle get(): LiveData<String> = _toolbarTitle
    val canComputerPlayersBeAdded get(): LiveData<Boolean> = _canComputerPlayersBeAdded
    val players get(): LiveData<List<PlayerWrUi>> = _players

    init {
        loadGame()
    }

    override fun onStart() {
        playersInWaitingRoom()
    }

    private fun loadGame() {
        disposableManager.add(
            facade.gameInfo()
                .observeOn(uiScheduler)
                .subscribe { gameInfo ->
                    _toolbarTitle.value = gameInfo.name
                    _canComputerPlayersBeAdded.value = gameInfo.gameIsNew
                }
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
