package ch.qscqlmpa.dwitch.ui.ingame.connection.guest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    private val _communicationState = mutableStateOf<GuestCommunicationState>(GuestCommunicationState.Disconnected)
    val connectionStatus get(): State<GuestCommunicationState> = _communicationState

    fun reconnect() {
        idlingResource.increment("Reconnection to the host (Comm state: connected)")
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
                .subscribe(
                    { state ->
                        _communicationState.value = state
                        idlingResource.decrement("Communication state updated ($state)")
                    },
                    { error -> Logger.error(error) { "Error while observing communication state." } }
                )
        )
    }
}
