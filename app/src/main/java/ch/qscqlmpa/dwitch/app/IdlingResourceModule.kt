package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitchcommonutil.MyIdlingResource
import dagger.Module
import dagger.Provides

@Module
class IdlingResourceModule {

    @AppScope
    @Provides
    fun provideIdlingResource(): MyIdlingResource {
        return object : MyIdlingResource {
            override fun isIdleNow(): Boolean {
                return true
            }

            override fun increment() {
                // Nothing to do: this is the production implementation
            }

            override fun decrement() {
                // Nothing to do: this is the production implementation
            }

        }
    }
}