package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWr
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

internal class WaitingRoomViewModel @Inject constructor(
    private val facade: WaitingRoomFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    fun playersInWaitingRoom(): LiveData<List<PlayerWr>> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observePlayers()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .doOnError { error -> Timber.e(error, "Error while observing connected players.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }
}
