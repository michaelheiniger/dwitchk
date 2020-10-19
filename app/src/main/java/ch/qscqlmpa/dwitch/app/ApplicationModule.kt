package ch.qscqlmpa.dwitch.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

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
}
