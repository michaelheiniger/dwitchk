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

    private val _players = MutableLiveData<List<PlayerWrUi>>(emptyList())
    val players get(): LiveData<List<PlayerWrUi>> = _players

    override fun onStart() {
        playersInWaitingRoom()
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
