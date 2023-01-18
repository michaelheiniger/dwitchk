package ch.qscqlmpa.dwitchcommunication.common

import ch.qscqlmpa.dwitchcommonutil.scheduler.DefaultSchedulerFactory
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.di.CommunicationScope
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class SchedulersModule {

    @CommunicationScope
    @Binds
    abstract fun provideSchedulerFactory(schedulerFactory: DefaultSchedulerFactory): SchedulerFactory
}
