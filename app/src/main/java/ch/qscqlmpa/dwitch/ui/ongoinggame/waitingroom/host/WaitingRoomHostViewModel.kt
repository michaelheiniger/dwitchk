package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.CancelGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LaunchGameUsecase
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.Resource
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

class WaitingRoomHostViewModel @Inject
constructor(
    private val hostCommunicator: HostCommunicator,
    private val gameLaunchableUsecase: GameLaunchableUsecase,
    private val launchGameUsecase: LaunchGameUsecase,
    private val cancelGameUsecase: CancelGameUsecase,
    private val gameEventRepository: GameEventRepository,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<WaitingRoomHostCommand>()

    fun currentCommunicationState(): LiveData<Resource> {
        return LiveDataReactiveStreams.fromPublisher(
            hostCommunicator.observeCommunicationState()
                .map(::getResourceForCommunicationState)
                .doOnError { error ->
                    Timber.e(
                        error,
                        "Error while observing communication state."
                    )
                }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun canGameBeLaunched(): LiveData<Boolean> {
        return LiveDataReactiveStreams.fromPublisher(
            gameLaunchableUsecase.gameCanBeLaunched()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .map(::processGameLaunchableEvent)
                .doOnError { error ->
                    Timber.e(
                        error,
                        "Error while observing if game can be launched."
                    )
                }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun commands(): LiveData<WaitingRoomHostCommand> {
        val liveDataMerger = MediatorLiveData<WaitingRoomHostCommand>()
        liveDataMerger.addSource(gameEventLiveData()) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun launchGame() {
        disposableManager.add(launchGameUsecase.launchGame()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .subscribe(
                { Timber.d("Game launched") },
                { error -> Timber.e(error, "Error while launching game") }
            )
        )
    }

    fun cancelGame() {
        disposableManager.add(
            cancelGameUsecase.cancelGame()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe()
        )
    }

    private fun processGameLaunchableEvent(event: GameLaunchableEvent): Boolean {
        return when (event) {
            GameLaunchableEvent.GameIsReadyToBeLaunched -> true
            GameLaunchableEvent.NotEnoughPlayers -> false
            GameLaunchableEvent.NotAllPlayersAreReady -> false
        }
    }

    private fun getResourceForCommunicationState(state: HostCommunicationState): Resource {
        val resourceId = when (state) {
            HostCommunicationState.LISTENING_FOR_GUESTS -> R.string.listening_for_guests
            HostCommunicationState.NOT_LISTENING_FOR_GUESTS -> R.string.not_listening_for_guests
            HostCommunicationState.ERROR -> R.string.error_listening_for_guests
        }
        return Resource(resourceId)
    }

    private fun gameEventLiveData(): LiveData<WaitingRoomHostCommand> {
        return LiveDataReactiveStreams.fromPublisher(
            gameEventRepository.observeEvents()
                .observeOn(schedulerFactory.ui())
                .map(::getCommandForGameEvent)
                .doOnError { error -> Timber.e(error, "Error while observing game events.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private fun getCommandForGameEvent(event: GameEvent): WaitingRoomHostCommand {
        return when (event) {
            GameEvent.GameCanceled -> WaitingRoomHostCommand.NavigateToHomeScreen
            GameEvent.GameOver -> WaitingRoomHostCommand.NothingToDo
            GameEvent.GameLaunched -> WaitingRoomHostCommand.NavigateToGameRoomScreen
        }
    }
}
