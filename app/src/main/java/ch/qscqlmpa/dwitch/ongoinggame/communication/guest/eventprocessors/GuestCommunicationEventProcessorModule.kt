package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class GuestCommunicationEventProcessorModule {

    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientCommunicationEvent.ConnectedToHost::class)
    internal abstract fun bindConnectedToHostEventProcessor(eventProcessorGuest: GuestConnectedToHostEventProcessor): GuestCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientCommunicationEvent.DisconnectedFromHost::class)
    internal abstract fun bindDisconnectedFromHostEventProcessor(eventProcessorGuest: GuestDisconnectedFromHostEventProcessor): GuestCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientCommunicationEvent.ConnectionError::class)
    internal abstract fun bindConnectionErrorEventProcessor(eventProcessorGuest: GuestConnectionErrorEventProcessor): GuestCommunicationEventProcessor

}