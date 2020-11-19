package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HostCommunicationEventProcessorModule {

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