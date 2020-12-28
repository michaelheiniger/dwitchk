package ch.qscqlmpa.dwitch.ui.base

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory

abstract class BaseViewModel(
    protected val disposableManager: DisposableManager,
    protected val schedulerFactory: SchedulerFactory
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        disposableManager.disposeAndReset()
    }
}
