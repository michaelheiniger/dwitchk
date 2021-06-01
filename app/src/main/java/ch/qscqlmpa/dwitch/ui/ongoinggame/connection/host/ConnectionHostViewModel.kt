package ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.common.HostGameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class ConnectionHostViewModel @Inject constructor(
    private val gameFacade: HostGameFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _connectionStatus = MutableLiveData<HostCommunicationState>()
    val connectionStatus get(): LiveData<HostCommunicationState> = _connectionStatus

    fun reconnect() {
        gameFacade.startServer()
    }

    override fun onStart() {
        super.onStart()
        currentCommunicationState()
    }

    private fun currentCommunicationState() {
        this.disposableManager.add(
            gameFacade.currentCommunicationState()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing communication state." } }
                .subscribe { state -> _connectionStatus.value = state }
        )
    }
}
