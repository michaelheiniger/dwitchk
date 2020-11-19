package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.events.HostCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Module
import dagger.Provides

@Module
internal class HostCommunicationModule {

    @Module
    companion object {

        @OngoingGameScope
        @Provides
        @JvmStatic
        fun provideHostCommunicator(
            commServer: CommServer,
            messageDispatcher: MessageDispatcher,
            hostCommunicationEventDispatcher: HostCommunicationEventDispatcher,
            communicationEventRepository: HostCommunicationEventRepository,
            localConnectionIdStore: LocalConnectionIdStore,
            schedulerFactory: SchedulerFactory
        ): HostCommunicator {
            return HostCommunicatorImpl(
                commServer,
                messageDispatcher,
                hostCommunicationEventDispatcher,
                communicationEventRepository,
                localConnectionIdStore,
                schedulerFactory
            )
        }
    }
}
