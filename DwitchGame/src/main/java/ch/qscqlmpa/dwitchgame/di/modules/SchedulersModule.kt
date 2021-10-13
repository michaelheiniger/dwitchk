package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchcommonutil.scheduler.DefaultSchedulerFactory
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.di.GameScope
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class SchedulersModule {

    @GameScope
    @Binds
    abstract fun provideSchedulerFactory(schedulerFactory: DefaultSchedulerFactory): SchedulerFactory
}
