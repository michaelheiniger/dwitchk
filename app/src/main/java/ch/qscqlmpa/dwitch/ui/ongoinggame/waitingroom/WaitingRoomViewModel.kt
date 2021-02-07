package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Scheduler
import timber.log.Timber
import javax.inject.Inject

internal class WaitingRoomViewModel @Inject constructor(
    private val facade: WaitingRoomFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    fun playersInWaitingRoom(): LiveData<List<PlayerWr>> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observePlayers()
                .observeOn(uiScheduler)
                .doOnError { error -> Timber.e(error, "Error while observing connected players.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }
}
