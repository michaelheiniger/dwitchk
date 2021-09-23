package ch.qscqlmpa.dwitchcommunication.common

import ch.qscqlmpa.dwitchcommonutil.scheduler.DefaultSchedulerFactory
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.di.CommunicationScope
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
internal class SchedulersModule {

    @CommunicationScope
    @Provides
    fun provideSchedulerFactory(): SchedulerFactory {
        return DefaultSchedulerFactory()
    }
}
