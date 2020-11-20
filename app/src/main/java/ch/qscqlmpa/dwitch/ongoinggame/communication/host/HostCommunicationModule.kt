package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.events.HostCommunicationStateRepository
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
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
            communicationStateRepository: HostCommunicationStateRepository,
            localConnectionIdStore: LocalConnectionIdStore,
            schedulerFactory: SchedulerFactory
        ): HostCommunicator {
            return HostCommunicatorImpl(
                commServer,
                messageDispatcher,
                hostCommunicationEventDispatcher,
                communicationStateRepository,
                localConnectionIdStore,
                schedulerFactory
            )
        }
    }
}
