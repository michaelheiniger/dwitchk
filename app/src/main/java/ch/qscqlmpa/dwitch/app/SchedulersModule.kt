package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitchcommonutil.scheduler.DefaultSchedulerFactory
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import dagger.Module
import dagger.Provides

@Module
class SchedulersModule {

    @AppScope
    @Provides
    fun provideSchedulerFactory(): SchedulerFactory {
        return DefaultSchedulerFactory()
    }
}