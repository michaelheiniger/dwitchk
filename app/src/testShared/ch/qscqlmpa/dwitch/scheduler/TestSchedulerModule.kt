package ch.qscqlmpa.dwitch.scheduler

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class TestSchedulerModule {

    @Singleton
    @Binds
    abstract fun bindSchedulerProvider(schedulerFactory: TestSchedulerFactory): SchedulerFactory
}