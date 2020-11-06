package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.PlayerReadyUsecase
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.Resource
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.reactivex.BackpressureStrategy
import timber.log.Timber
import javax.inject.Inject

class WaitingRoomGuestViewModel @Inject
constructor(private val guestCommunicator: GuestCommunicator,
            private val playerReadyUsecase: PlayerReadyUsecase,
            private val gameEventRepository: GameEventRepository,
            disposableManager: DisposableManager,
            schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<WaitingRoomGuestCommand>()

    //TODO: Handle connection error / disconnection events / ...
    fun currentCommunicationState(): LiveData<Resource> {
        return LiveDataReactiveStreams.fromPublisher(
                guestCommunicator.observeCommunicationState()
                        .subscribeOn(schedulerFactory.io())
                        .observeOn(schedulerFactory.ui())
                        .map(::getResourceForCommunicationState)
                        .doOnError { error -> Timber.e(error, "Error while observing communication state.") }
                        .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun updateReadyState(ready: Boolean) {
        disposableManager.add(playerReadyUsecase.updateReadyState(ready)
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe()
        )
    }

    fun commands(): LiveData<WaitingRoomGuestCommand> {
        val liveDataMerger = MediatorLiveData<WaitingRoomGuestCommand>()
        liveDataMerger.addSource(gameEventLiveData()) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(commands) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun userAcknowledgesGameCanceledEvent() {
        commands.value = WaitingRoomGuestCommand.NavigateToHomeScreen
    }

    private fun gameEventLiveData(): LiveData<WaitingRoomGuestCommand> {
        return LiveDataReactiveStreams.fromPublisher(
                gameEventRepository.observeEvents()
                        .observeOn(schedulerFactory.ui())
                        .map(::getCommandForGameEvent)
                        .doOnError { error -> Timber.e(error, "Error while observing game events.") }
                        .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    private fun getResourceForCommunicationState(state: GuestCommunicationState): Resource {
        val resourceId = when (state) {
            GuestCommunicationState.CONNECTED -> R.string.connected_to_host
            GuestCommunicationState.DISCONNECTED -> R.string.disconnected_from_host
            GuestCommunicationState.ERROR -> R.string.connection_error_with_host
        }
        return Resource(resourceId)
    }

    private fun getCommandForGameEvent(event: GameEvent): WaitingRoomGuestCommand {
        return when (event) {
            GameEvent.GameCanceled -> WaitingRoomGuestCommand.NotifyUserGameCanceled
            GameEvent.GameLaunched -> WaitingRoomGuestCommand.NavigateToGameRoomScreen
            GameEvent.GameOver -> WaitingRoomGuestCommand.NotifyUserGameOver
        }
    }
}
