package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Module
import dagger.Provides

@Module
class HostCommunicationModule {

    @Module
    companion object {

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun provideHostCommunicator(
                commServer: CommServer,
                messageDispatcher: MessageDispatcher,
                hostCommunicationEventDispatcher: HostCommunicationEventDispatcher,
                schedulerFactory: SchedulerFactory,
                localConnectionIdStore: LocalConnectionIdStore
        ): HostCommunicator {
            return HostCommunicatorImpl(
                commServer,
                messageDispatcher,
                hostCommunicationEventDispatcher,
                schedulerFactory,
                localConnectionIdStore
            )
        }
    }
}
