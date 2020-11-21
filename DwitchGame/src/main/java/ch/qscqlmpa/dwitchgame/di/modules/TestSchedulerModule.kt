package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class TestSchedulerModule {

    @Singleton
    @Binds
    abstract fun bindSchedulerProvider(schedulerFactory: TestSchedulerFactory): SchedulerFactory
}