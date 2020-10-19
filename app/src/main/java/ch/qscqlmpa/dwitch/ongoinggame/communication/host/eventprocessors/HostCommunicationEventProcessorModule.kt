package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ClientConnected
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ClientDisconnected
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HostCommunicationEventProcessorModule {

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ClientConnected::class)
    internal abstract fun bindClientConnectedEventProcessor(
            eventProcessor: GuestConnectedEventProcessor
    ): HostCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @HostCommunicationEventProcessorKey(ClientDisconnected::class)
    internal abstract fun bindClientDisconnectedEventProcessor(
            eventProcessor: GuestDisconnectedEventProcessor
    ): HostCommunicationEventProcessor
}