package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules


import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicatorImpl
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
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
            connectionStore: ConnectionStore,
            schedulerFactory: SchedulerFactory
        ): HostCommunicator {
            return HostCommunicatorImpl(
                commServer,
                messageDispatcher,
                hostCommunicationEventDispatcher,
                communicationStateRepository,
                connectionStore,
                schedulerFactory
            )
        }
    }
}
