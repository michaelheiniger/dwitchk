package ch.qscqlmpa.dwitch.ui.ingame.waitingroom

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.toolbarDefaultTitle
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class WaitingRoomViewModel @Inject constructor(
    private val facade: WaitingRoomFacade,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _toolbarTitle = mutableStateOf(toolbarDefaultTitle)
    private val _canComputerPlayersBeAdded = mutableStateOf(false)
    private val _players = mutableStateOf<List<PlayerWrUi>>(emptyList())

    val toolbarTitle get(): State<String> = _toolbarTitle
    val canComputerPlayersBeAdded get(): State<Boolean> = _canComputerPlayersBeAdded
    val players get(): State<List<PlayerWrUi>> = _players

    init {
        loadGame()
    }

    override fun onStart() {
        super.onStart()
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
        idlingResource.increment("Initial state of WR players")
        disposableManager.add(
            facade.observePlayers()
                .distinctUntilChanged()
                .doOnNext { players ->
                    idlingResource.decrement("State of WR players is updated ($players)")
                    Logger.debug { "players updated: $players" }
                }
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing connected players." } }
                .subscribe { players -> _players.value = players }
        )
    }
}
