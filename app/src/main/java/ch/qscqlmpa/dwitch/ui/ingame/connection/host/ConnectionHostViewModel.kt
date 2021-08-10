package ch.qscqlmpa.dwitch.ui.ingame.connection.host

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.ingame.common.HostGameFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class ConnectionHostViewModel @Inject constructor(
    private val gameFacade: HostGameFacade,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _connectionStatus = mutableStateOf<HostCommunicationState>(HostCommunicationState.Closed)
    val connectionStatus get(): State<HostCommunicationState> = _connectionStatus

    fun reconnect() {
        gameFacade.startServer()
    }

    override fun onStart() {
        super.onStart()
        currentCommunicationState()
    }

    private fun currentCommunicationState() {
        idlingResource.increment("Initial communication state")
        this.disposableManager.add(
            gameFacade.currentCommunicationState()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing communication state." } }
                .subscribe { state ->
                    _connectionStatus.value = state
                    idlingResource.decrement("Communication state updated ($state)")
                }
        )
    }
}
