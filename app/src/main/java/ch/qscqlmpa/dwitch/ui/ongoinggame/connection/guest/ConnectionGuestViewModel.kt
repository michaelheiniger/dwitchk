package ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ongoinggame.GuestFacade
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.model.UiInfoModel
import ch.qscqlmpa.dwitch.ui.model.Visibility
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import timber.log.Timber
import javax.inject.Inject

class ConnectionGuestViewModel @Inject constructor(
    private val facade: GuestFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val reconnectActionCtrl = MutableLiveData<UiControlModel>()
    private val reconnectLoadingCtrl = MutableLiveData<UiControlModel>()

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

    fun connectionStateInfo(): LiveData<UiInfoModel> {
        return LiveDataReactiveStreams.fromPublisher(currentCommunicationState().map { state -> UiInfoModel(state.resourceId) })
    }

    fun reconnect() {
        reconnectActionCtrl.value = UiControlModel(enabled = false)
        reconnectLoadingCtrl.value = UiControlModel(visibility = Visibility.Visible)
        facade.connect()
    }

    private fun currentCommunicationState(): Flowable<GuestCommunicationState> {
        return facade.observeCommunicationState()
            .subscribeOn(schedulerFactory.io())
            .observeOn(schedulerFactory.ui())
            .doOnError { error -> Timber.e(error, "Error while observing communication state.") }
            .toFlowable(BackpressureStrategy.LATEST)
    }
}
