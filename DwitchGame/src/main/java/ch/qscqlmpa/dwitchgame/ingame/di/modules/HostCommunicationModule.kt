package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommunication.websocket.ServerEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.host.*
import ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors.*
import ch.qscqlmpa.dwitchgame.ingame.di.HostCommunicationEventProcessorKey
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
internal abstract class HostCommunicationModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideCommunicationFacade(facade: HostCommunicationFacadeImpl): HostCommunicationFacade

    @OngoingGameScope
    @Binds
    internal abstract fun provideCommunicator(hostCommunicatorImpl: HostCommunicatorImpl): HostCommunicator

    @OngoingGameScope
    @Binds
    internal abstract fun provideComputerCommunicator(hostCommunicatorImpl: HostCommunicatorImpl): ComputerCommunicator

    // ##### HostCommunicationEventProcessor implementations #####
    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerEvent.CommunicationEvent.ListeningForConnections::class)
    internal abstract fun bindHostListeningForConnectionsEventProcessor(
        eventProcessor: HostListeningForConnectionsEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerEvent.CommunicationEvent.NoLongerListeningForConnections::class)
    internal abstract fun bindHostNoLongerListeningForConnectionsEventProcessor(
        eventProcessor: HostNoLongerListeningForConnectionsEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerEvent.CommunicationEvent.ClientConnected::class)
    internal abstract fun bindClientConnectedEventProcessor(
        eventProcessor: GuestConnectedEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerEvent.CommunicationEvent.ClientDisconnected::class)
    internal abstract fun bindClientDisconnectedEventProcessor(
        eventProcessor: GuestDisconnectedEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerEvent.CommunicationEvent.ErrorListeningForConnections::class)
    internal abstract fun bindErrorListeningForConnections(
        eventProcessor: ErrorListeningForConnectionsEventProcessor
    ): HostCommunicationEventProcessor
}
