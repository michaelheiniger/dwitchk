package ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.common.GuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class ConnectionGuestViewModel @Inject constructor(
    private val facade: GuestFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _communicationState = MutableLiveData<GuestCommunicationState>()
    val communicationState get(): LiveData<GuestCommunicationState> = _communicationState

    fun reconnect() {
        facade.connect()
    }

    override fun onStart() {
        super.onStart()
        currentCommunicationState()
    }

    override fun onStop() {
        super.onStop()
        this.disposableManager.disposeAndReset()
    }

    private fun currentCommunicationState() {
        this.disposableManager.add(
            facade.currentCommunicationState()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing communication state." } }
                .subscribe { state -> _communicationState.value = state }
        )
    }
}
