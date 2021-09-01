package ch.qscqlmpa.dwitch.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import org.tinylog.kotlin.Logger

abstract class BaseViewModel : ViewModel() {

    protected val disposableManager = DisposableManager()

    @CallSuper
    open fun onStart() {
        Logger.debug { "${this::class.java}.onStart()" }
    }

    @CallSuper
    open fun onStop() {
        Logger.debug { "${this::class.java}.onStop()" }
        disposableManager.disposeAndReset()
    }
}
