package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchcommonutil.RealTimeProvider
import ch.qscqlmpa.dwitchcommonutil.TimeProvider
import ch.qscqlmpa.dwitchgame.di.GameScope
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
internal object UtilsModule {

    @GameScope
    @Provides
    fun providesTimeProvider(): TimeProvider {
        return RealTimeProvider()
    }
}
