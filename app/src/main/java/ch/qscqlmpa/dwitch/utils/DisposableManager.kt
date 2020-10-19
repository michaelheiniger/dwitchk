package ch.qscqlmpa.dwitch.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

open class DisposableManager @Inject
constructor() {

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
