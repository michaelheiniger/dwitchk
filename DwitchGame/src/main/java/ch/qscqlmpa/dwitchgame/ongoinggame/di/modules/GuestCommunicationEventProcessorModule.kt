package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules


import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestConnectedToHostEventProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestConnectionErrorEventProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestDisconnectedFromHostEventProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.di.GuestCommunicationEventProcessorKey
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
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