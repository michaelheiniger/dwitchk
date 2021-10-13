package ch.qscqlmpa.dwitch.app

import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler

@Suppress("unused")
@Module
object ApplicationModule {

    @AppScope
    @Provides
    fun provideUiScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}
