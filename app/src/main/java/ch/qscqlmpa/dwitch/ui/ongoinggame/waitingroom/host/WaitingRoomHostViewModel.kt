package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.usecases.CancelGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LaunchGameUsecase
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
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
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<WaitingRoomHostCommand>()

    fun currentCommunicationState(): LiveData<HostCommunicationState> {
        return LiveDataReactiveStreams.fromPublisher(
            hostCommunicator.observeCommunicationState()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .doOnError { error -> Timber.e(error, "Error while observing communication state.") }
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
                    Timber.e(error, "Error while observing if game can be launched.")
                }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun commands(): LiveData<WaitingRoomHostCommand> {
        val liveDataMerger = MediatorLiveData<WaitingRoomHostCommand>()
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun launchGame() {
        disposableManager.add(launchGameUsecase.launchGame()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .subscribe(
                {
                    Timber.i("Game launched")
                    commands.value = WaitingRoomHostCommand.NavigateToGameRoomScreen
                },
                { error -> Timber.e(error, "Error while launching game") }
            )
        )
    }

    fun cancelGame() {
        disposableManager.add(
            cancelGameUsecase.cancelGame()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    {
                        Timber.i("Game canceled")
                        commands.value = WaitingRoomHostCommand.NavigateToHomeScreen
                    },
                    { error -> Timber.e(error, "Error while canceling game") }
                )
        )
    }

    private fun processGameLaunchableEvent(event: GameLaunchableEvent): Boolean {
        return when (event) {
            GameLaunchableEvent.GameIsReadyToBeLaunched -> true
            GameLaunchableEvent.NotEnoughPlayers -> false
            GameLaunchableEvent.NotAllPlayersAreReady -> false
        }
    }
}
