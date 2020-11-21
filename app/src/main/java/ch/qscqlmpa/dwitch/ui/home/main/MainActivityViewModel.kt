package ch.qscqlmpa.dwitch.ui.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGameRepository
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

class MainActivityViewModel @Inject
constructor(
    private val gameRepository: AdvertisedGameRepository,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    fun observeAdvertisedGames(): LiveData<AdvertisedGameResponse> {
        return LiveDataReactiveStreams.fromPublisher(
            gameRepository.listenForAdvertisedGames()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .map { games -> AdvertisedGameResponse.success(games) }
                .onErrorReturn { error -> AdvertisedGameResponse.error(error) }
                .doOnError { error -> Timber.e(error, "Error while observing connected players.") }
                .doFinally { gameRepository.stopListening() }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }
}
