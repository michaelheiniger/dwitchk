package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class JoinNewGameViewModel @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val guestFacade: HomeGuestFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _navigation = MutableLiveData<JoinNewGameDestination>()
    private val _loading = MutableLiveData<Boolean>()
    private val _joinGameControl = MutableLiveData(false)
    private val _playerName = MutableLiveData("")

    private lateinit var game: AdvertisedGame

    init {
        if (BuildConfig.DEBUG) {
            _joinGameControl.value = true
            _playerName.value = "Mébène"
        }
        Logger.debug { "Viewmodel lifecycle event: create JoinNewGameViewModel ($this)" }
    }

    val navigationCommand get(): LiveData<JoinNewGameDestination> = _navigation
    val loading get(): LiveData<Boolean> = _loading
    val playerName get(): LiveData<String> = _playerName
    val joinGameControl get(): LiveData<Boolean> = _joinGameControl

    //TODO: find better solution without side effet (i.e. assignment)
    fun getGame(ipAddress: String): AdvertisedGame {
        game = guestFacade.getAdvertisedGame(ipAddress)
        return game
    }

    fun onPlayerNameChange(value: String) {
        _playerName.value = value
        _joinGameControl.value = !playerName.value.isNullOrBlank()
    }

    fun joinGame() {
        val playerName = playerName.value
        require(!playerName.isNullOrBlank()) { "Player name cannot be blank" }
        _loading.value = true
        disposableManager.add(
            Completable.merge(
                listOf(
                    appEventRepository.observeEvents()
                        .filter { event -> event is AppEvent.ServiceStarted }
                        .firstElement()
                        .ignoreElement(),
                    guestFacade.joinGame(game, playerName)
                )
            )
                .observeOn(uiScheduler)
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    {
                        _navigation.value = JoinNewGameDestination.NavigateToWaitingRoom
                    },
                    { error -> Logger.error(error) { "Error while joining the game" } }
                )
        )
    }

    override fun onCleared() {
        Logger.debug { "Viewmodel lifecycle event: clear JoinNewGameViewModel ($this)" }
        super.onCleared()
    }
}
