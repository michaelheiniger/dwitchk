package ch.qscqlmpa.dwitch.ui.base

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager

abstract class BaseViewModel(protected val disposableManager: DisposableManager,
                             protected val schedulerFactory: SchedulerFactory
) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        disposableManager.disposeAndReset()
    }
}
