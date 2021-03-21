package ch.qscqlmpa.dwitch.ui.base

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager

abstract class BaseViewModel : ViewModel() {

    protected val disposableManager = DisposableManager()

    override fun onCleared() {
        super.onCleared()
        disposableManager.disposeAndReset()
    }

    open fun onStart() {

    }

    open fun onStop() {

    }
}
