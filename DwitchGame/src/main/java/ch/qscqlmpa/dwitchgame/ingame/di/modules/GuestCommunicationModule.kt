package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommunication.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicatorImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors.*
import ch.qscqlmpa.dwitchgame.ingame.di.GuestCommunicationEventProcessorKey
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
internal abstract class GuestCommunicationModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGuestCommunicationFacade(facade: GuestCommunicationFacadeImpl): GuestCommunicationFacade

    @OngoingGameScope
    @Binds
    internal abstract fun provideGuestCommunicator(communicator: GuestCommunicatorImpl): GuestCommunicator


    // ##### GuestCommunicationEventProcessor implementations #####
    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientEvent.CommunicationEvent.ConnectedToHost::class)
    internal abstract fun bindConnectedToHostEventProcessor(
        eventProcessorGuest: GuestConnectedToHostEventProcessor
    ): GuestCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientEvent.CommunicationEvent.DisconnectedFromHost::class)
    internal abstract fun bindDisconnectedFromHostEventProcessor(
        eventProcessorGuest: GuestDisconnectedFromHostEventProcessor
    ): GuestCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientEvent.CommunicationEvent.ConnectionError::class)
    internal abstract fun bindConnectionErrorEventProcessor(
        eventProcessorGuest: GuestConnectionErrorEventProcessor
    ): GuestCommunicationEventProcessor

    @OngoingGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientEvent.CommunicationEvent.Stopped::class)
    internal abstract fun bindStoppedEventProcessor(
        eventProcessorGuest: GuestStoppedEventProcessor
    ): GuestCommunicationEventProcessor
}
