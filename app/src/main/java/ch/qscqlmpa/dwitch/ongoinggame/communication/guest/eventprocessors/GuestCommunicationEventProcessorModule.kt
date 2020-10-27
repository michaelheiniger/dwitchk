package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ConnectedToHost
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.DisconnectedFromHost
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class GuestCommunicationEventProcessorModule {

    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ConnectedToHost::class)
    internal abstract fun bindConnectedToHostEventProcessor(eventProcessorGuest: GuestConnectedToHostEventProcessor): GuestCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(DisconnectedFromHost::class)
    internal abstract fun bindDisconnectedFromHostEventProcessor(eventProcessorGuest: GuestDisconnectedFromHostEventProcessor): GuestCommunicationEventProcessor

}