package ch.qscqlmpa.dwitch.ui.base

import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import org.tinylog.kotlin.Logger

abstract class BaseViewModel : ViewModel() {

    protected val disposableManager = DisposableManager()

    override fun onCleared() {
        super.onCleared()
        disposableManager.disposeAndReset()
    }

    open fun onStart() {
        Logger.debug { "${this::class.java}.onStart()" }
    }

    open fun onStop() {
        Logger.debug { "${this::class.java}.onStop()" }
        disposableManager.disposeAndReset()
    }
}
