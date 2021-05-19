package ch.qscqlmpa.dwitch.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler

@Suppress("unused")
@Module
class ApplicationModule(private val application: Application) {

    /**
     * Useful to access sharedPreferences, system services, ...
     * @return
     */
    @Provides
    fun provideApplicationContext(): Context {
        return application
    }

    @AppScope
    @Provides
    fun provideUiScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}
