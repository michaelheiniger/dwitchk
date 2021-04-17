package ch.qscqlmpa.dwitchcommonutil

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class DisposableManager {

    private var compositeDisposable = CompositeDisposable()

    fun add(vararg disposables: Disposable) {
        compositeDisposable.addAll(*disposables)
    }

    fun dispose() {
        compositeDisposable.dispose()
    }

    fun disposeAndReset() {
        dispose()
        compositeDisposable = CompositeDisposable()
    }
}
