package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitch.app.AppScope
import ch.qscqlmpa.dwitchcommonutil.MyIdlingResource
import dagger.Module
import dagger.Provides

@Module
class TestIdlingResourceModule {

    @AppScope
    @Provides
    fun provideIdlingResource(): MyIdlingResource {
        return IdlingResourceAdapter()
    }
}