package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchgame.ingame.communication.host.ComputerCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicatorImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
internal class HostCommunicationModule {

    companion object {

        @OngoingGameScope
        @Provides
        fun provideHostCommunicatorImpl(
            commServer: CommServer,
            messageDispatcher: MessageDispatcher,
            hostCommunicationEventDispatcher: HostCommunicationEventDispatcher,
            communicationStateRepository: HostCommunicationStateRepository,
            schedulerFactory: SchedulerFactory
        ): HostCommunicatorImpl {
            return HostCommunicatorImpl(
                commServer,
                messageDispatcher,
                hostCommunicationEventDispatcher,
                communicationStateRepository,
                schedulerFactory
            )
        }

        @OngoingGameScope
        @Provides
        fun provideHostCommunicator(hostCommunicatorImpl: HostCommunicatorImpl): HostCommunicator {
            return hostCommunicatorImpl
        }

        @OngoingGameScope
        @Provides
        fun provideComputerCommClient(hostCommunicatorImpl: HostCommunicatorImpl): ComputerCommunicator {
            return hostCommunicatorImpl
        }
    }
}
