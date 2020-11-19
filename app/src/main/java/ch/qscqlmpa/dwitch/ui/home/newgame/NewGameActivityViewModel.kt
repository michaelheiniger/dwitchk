package ch.qscqlmpa.dwitch.ui.home.newgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.home.HomeGuestFacade
import ch.qscqlmpa.dwitch.home.HomeHostFacade
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import timber.log.Timber
import javax.inject.Inject

class NewGameActivityViewModel @Inject
constructor(
    private val hostFacade: HomeHostFacade,
    private val guestFacade: HomeGuestFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val errors = MutableLiveData<List<NewGameError>>()
    private val event = MutableLiveData<NewGameEvent>()

    fun observeErrors(): LiveData<List<NewGameError>> {
        return errors
    }

    fun observeEvents(): LiveData<NewGameEvent> {
        return event
    }

    fun nextForGuest(advertisedGame: AdvertisedGame, playerName: String) {
        val validationErrors = NewGameValidator.validate(advertisedGame.gameName, playerName)
        if (validationErrors.isNotEmpty()) {
            errors.setValue(validationErrors)
        } else {
            joinGame(advertisedGame, playerName)
        }
    }

    fun nextForHost(gameName: String, playerName: String) {
        val validationErrors = NewGameValidator.validate(gameName, playerName)
        if (validationErrors.isNotEmpty()) {
            errors.setValue(validationErrors)
        } else {
            hostGame(gameName, playerName)
        }
    }

    private fun joinGame(advertisedGame: AdvertisedGame, playerName: String) {
        disposableManager.add(
            guestFacade.joinGame(advertisedGame, playerName)
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    { event.setValue(NewGameEvent.SETUP_SUCCESSFUL) },
                    { error -> Timber.e(error, "Error while joining the game") }
                )
        )
    }

    private fun hostGame(gameName: String, playerName: String) {
        disposableManager.add(
            hostFacade.hostGame(gameName, playerName)
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    { event.setValue(NewGameEvent.SETUP_SUCCESSFUL) },
                    { error -> Timber.e(error, "Error while start hosting the game") }
                )
        )
    }
}
