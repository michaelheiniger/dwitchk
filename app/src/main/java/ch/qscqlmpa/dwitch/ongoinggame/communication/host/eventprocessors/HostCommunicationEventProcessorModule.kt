package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

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
}