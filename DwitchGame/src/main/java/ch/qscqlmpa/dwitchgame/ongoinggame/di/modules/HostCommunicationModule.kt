package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.ComputerCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicatorImpl
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
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
            schedulerFactory: SchedulerFactory,
            idlingResource: DwitchIdlingResource
        ): HostCommunicatorImpl {
            return HostCommunicatorImpl(
                commServer,
                messageDispatcher,
                hostCommunicationEventDispatcher,
                communicationStateRepository,
                schedulerFactory,
                idlingResource
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
