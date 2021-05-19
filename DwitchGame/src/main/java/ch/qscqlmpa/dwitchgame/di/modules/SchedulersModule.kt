package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchcommonutil.scheduler.DefaultSchedulerFactory
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.di.GameScope
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
internal class SchedulersModule {

    @GameScope
    @Provides
    fun provideSchedulerFactory(): SchedulerFactory {
        return DefaultSchedulerFactory()
    }
}
