package ch.qscqlmpa.dwitch.ui.ongoinggame.endofround

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.game.EndOfRoundInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class EndOfRoundViewModel @Inject constructor(
    private val facade: GameDashboardFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _endOfRoundInfo = MutableLiveData<EndOfRoundInfo>()

    val endOfRoundInfo get(): LiveData<EndOfRoundInfo> = _endOfRoundInfo

    override fun onStart() {
        super.onStart()
        observeEndOfRoundInfo()
    }

    override fun onStop() {
        super.onStop()
        disposableManager.disposeAndReset()
    }

    private fun observeEndOfRoundInfo() {
        disposableManager.add(
            facade.getEndOfRoundInfo()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing dashboard info." } }
                .subscribe { value -> _endOfRoundInfo.value = value }
        )
    }
}
