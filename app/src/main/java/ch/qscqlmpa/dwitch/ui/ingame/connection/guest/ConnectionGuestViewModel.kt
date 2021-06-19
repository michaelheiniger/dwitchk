package ch.qscqlmpa.dwitch.ui.ingame.connection.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.ingame.common.GuestGameFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class ConnectionGuestViewModel @Inject constructor(
    private val gameFacade: GuestGameFacade,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _communicationState = MutableLiveData<GuestCommunicationState>()
    val connectionStatus get(): LiveData<GuestCommunicationState> = _communicationState

    fun reconnect() {
        gameFacade.connect()
    }

    override fun onStart() {
        super.onStart()
        currentCommunicationState()
    }

    private fun currentCommunicationState() {
        idlingResource.increment("Initial communication state")
        this.disposableManager.add(
            gameFacade.currentCommunicationState()
                .distinctUntilChanged()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing communication state." } }
                .subscribe { state ->
                    _communicationState.value = state
                    idlingResource.decrement("Communication state updated ($state)")
                }
        )
    }
}
