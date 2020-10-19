package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWrRepository
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWr
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.BaseViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

class WaitingRoomViewModel @Inject
constructor(private val playerWrRepository: PlayerWrRepository,
            disposableManager: DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    fun connectedPlayers(): LiveData<List<PlayerWr>> {
        return LiveDataReactiveStreams.fromPublisher(
                playerWrRepository.observeConnectedPlayers()
                        .subscribeOn(schedulerFactory.io())
                        .observeOn(schedulerFactory.ui())
                        .doOnError { error -> Timber.e(error, "Error while observing connected players.") }
                        .toFlowable(BackpressureStrategy.LATEST)
        )
    }
}
