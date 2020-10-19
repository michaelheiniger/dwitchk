package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LaunchGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.CancelGameUsecase
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.Resource
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

class WaitingRoomHostViewModel @Inject
constructor(private val hostCommunicator: HostCommunicator,
            private val gameLaunchableUsecase: GameLaunchableUsecase,
            private val launchGame: LaunchGameUsecase,
            private val cancelGameUsecase: CancelGameUsecase,
            disposableManager: DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<WaitingRoomHostCommand>()

    init {
        openRoomToGuests()
    }

    fun currentCommunicationState(): LiveData<Resource> {
        return LiveDataReactiveStreams.fromPublisher(
                hostCommunicator.observeCommunicationState()
                        .map(::getResourceForCommunicationState)
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
                        .doOnError { error -> Timber.e(error, "Error while observing if game can be launched.") }
                        .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun commands(): LiveData<WaitingRoomHostCommand> {
        return commands
    }

    fun launchGame() {
        disposableManager.add(launchGame.launchGame()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                        {
                            Timber.d("Game launched")
                            commands.value = WaitingRoomHostCommand.NavigateToGameRoomScreen
                        },
                        { error -> Timber.e(error, "Error while launching game") }
                )
        )
    }

    fun cancelGame() {
        disposableManager.add(cancelGameUsecase.cancelGame()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe { commands.value = WaitingRoomHostCommand.NavigateToHomeScreen }
        )
    }

    private fun processGameLaunchableEvent(event: GameLaunchableEvent): Boolean {
        return when (event) {
            GameLaunchableEvent.GameIsReadyToBeLaunched -> true
            GameLaunchableEvent.NotEnoughPlayers -> false
            GameLaunchableEvent.NotAllPlayersAreReady -> false
        }
    }

    private fun openRoomToGuests() {
        Timber.d("openRoomToGuests() !")
        hostCommunicator.listenForConnections()
    }

    private fun getResourceForCommunicationState(state: HostCommunicationState): Resource {
        val resourceId = when (state) {
            HostCommunicationState.LISTENING_FOR_GUESTS -> R.string.listening_for_guests
            HostCommunicationState.NOT_LISTENING_FOR_GUESTS -> R.string.not_listening_for_guests
            HostCommunicationState.ERROR -> R.string.error_listening_for_guests
        }
        return Resource(resourceId)
    }
}
