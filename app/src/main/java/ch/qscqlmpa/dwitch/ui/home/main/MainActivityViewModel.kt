package ch.qscqlmpa.dwitch.ui.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.BaseViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import javax.inject.Inject

class MainActivityViewModel @Inject
constructor(private val gameRepository: AdvertisedGameRepository,
            disposableManager: DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val advertisedGames = MutableLiveData<AdvertisedGameResponse>()

    private var observingGames = false

    fun observeAdvertisedGames(): LiveData<AdvertisedGameResponse> {
        if (!observingGames) {
            startObservingAdvertisedGames()
            observingGames = true
        }
        return advertisedGames
    }

    private fun startObservingAdvertisedGames() {
        disposableManager.add(gameRepository.listenForAdvertisedGames()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                        { games -> advertisedGames.setValue(AdvertisedGameResponse.success(games)) },
                        { error -> advertisedGames.setValue(AdvertisedGameResponse.error(error)) }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        gameRepository.stopListening()
    }
}
