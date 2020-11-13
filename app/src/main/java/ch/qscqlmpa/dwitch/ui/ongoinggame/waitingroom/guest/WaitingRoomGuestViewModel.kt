package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LeaveGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.PlayerReadyUsecase
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.Resource
import ch.qscqlmpa.dwitch.ui.model.CheckboxModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.model.Visibility
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject

class WaitingRoomGuestViewModel @Inject
constructor(
    private val guestCommunicator: GuestCommunicator,
    private val playerReadyUsecase: PlayerReadyUsecase,
    private val leaveGameUsecase: LeaveGameUsecase,
    private val gameEventRepository: GuestGameEventRepository,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val commands = MutableLiveData<WaitingRoomGuestCommand>()

    private val reconnectActionCtrl = MutableLiveData<UiControlModel>()
    private val reconnectLoadingCtrl = MutableLiveData<UiControlModel>()
    private val leaveGameActionCtrl = MutableLiveData<UiControlModel>()

    fun localPlayerReadyStateInfo(): LiveData<CheckboxModel> {
        return LiveDataReactiveStreams.fromPublisher(
            currentCommunicationState()
                .map { state ->
                    when (state) {
                        GuestCommunicationState.Connected -> CheckboxModel(enabled = true, checked = true) //FIXME: the "checked" must indeed be set but it's currently hard-coded
                        GuestCommunicationState.Disconnected,
                        GuestCommunicationState.Error -> CheckboxModel(enabled = false, checked = false)
                    }
                }
        )
    }

    fun reconnectAction(): LiveData<UiControlModel> {
        val liveDataMerger = MediatorLiveData<UiControlModel>()
        liveDataMerger.addSource(
            LiveDataReactiveStreams.fromPublisher(
                currentCommunicationState()
                    .map { state ->
                        when (state) {
                            GuestCommunicationState.Connected -> UiControlModel(visibility = Visibility.Gone)
                            GuestCommunicationState.Disconnected,
                            GuestCommunicationState.Error -> UiControlModel(visibility = Visibility.Visible)
                        }
                    }
            )
        ) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(reconnectActionCtrl) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun reconnectLoading(): LiveData<UiControlModel> {
        val liveDataMerger = MediatorLiveData<UiControlModel>()
        liveDataMerger.addSource(
            LiveDataReactiveStreams.fromPublisher(currentCommunicationState().map { UiControlModel(visibility = Visibility.Gone) })
        ) { value -> liveDataMerger.value = value }
        liveDataMerger.addSource(reconnectLoadingCtrl) { value -> liveDataMerger.value = value }
        return liveDataMerger
    }

    fun connectionState(): LiveData<Resource> {
        return LiveDataReactiveStreams.fromPublisher(currentCommunicationState().map(GuestCommunicationState::resourceId))
    }

    private fun currentCommunicationState(): Flowable<GuestCommunicationState> {
        return guestCommunicator.observeCommunicationState()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .doOnError { error -> Timber.e(error, "Error while observing communication state.") }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    fun reconnect() {
        reconnectActionCtrl.value = UiControlModel(enabled = false)
        reconnectLoadingCtrl.value = UiControlModel(visibility = Visibility.Visible)
        guestCommunicator.connect()
    }

    fun updateReadyState(ready: Boolean) {
        disposableManager.add(
            playerReadyUsecase.updateReadyState(ready)
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

    fun leaveGame() {
        disposableManager.add(
            leaveGameUsecase.leaveGame()
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    {
                        Timber.i("Left game successfully")
                        commands.value = WaitingRoomGuestCommand.NavigateToHomeScreen
                    },
                    { error -> Timber.e(error, "Error while leaving game") }
                )
        )
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

    private fun getCommandForGameEvent(event: GuestGameEvent): WaitingRoomGuestCommand {
        return when (event) {
            GuestGameEvent.GameCanceled -> WaitingRoomGuestCommand.NotifyUserGameCanceled
            GuestGameEvent.GameLaunched -> WaitingRoomGuestCommand.NavigateToGameRoomScreen
            GuestGameEvent.GameOver -> throw IllegalStateException("Event '$event' is not supposed to occur in WaitingRoom.")
        }
    }
}
