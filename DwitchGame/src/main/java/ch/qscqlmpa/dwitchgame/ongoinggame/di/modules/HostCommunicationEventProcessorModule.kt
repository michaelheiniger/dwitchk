package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.*
import ch.qscqlmpa.dwitchgame.ongoinggame.di.HostCommunicationEventProcessorKey
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class HostCommunicationEventProcessorModule {

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerCommunicationEvent.ListeningForConnections::class)
    internal abstract fun bindHostListeningForConnectionsEventProcessor(
        eventProcessor: HostListeningForConnectionsEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerCommunicationEvent.NoLongerListeningForConnections::class)
    internal abstract fun bindHostNoLongerListeningForConnectionsEventProcessor(
        eventProcessor: HostNoLongerListeningForConnectionsEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerCommunicationEvent.ClientConnected::class)
    internal abstract fun bindClientConnectedEventProcessor(
        eventProcessor: GuestConnectedEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerCommunicationEvent.ClientDisconnected::class)
    internal abstract fun bindClientDisconnectedEventProcessor(
        eventProcessor: GuestDisconnectedEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ServerCommunicationEvent.ErrorListeningForConnections::class)
    internal abstract fun bindErrorListeningForConnections(
        eventProcessor: ErrorListeningForConnectionsEventProcessor
    ): HostCommunicationEventProcessor
}
