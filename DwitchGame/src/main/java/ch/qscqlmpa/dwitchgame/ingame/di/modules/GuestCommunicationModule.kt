package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicatorImpl
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors.*
import ch.qscqlmpa.dwitchgame.ingame.di.GuestCommunicationEventProcessorKey
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
internal abstract class GuestCommunicationModule {

    @InGameScope
    @Binds
    internal abstract fun provideGuestCommunicationFacade(facade: GuestCommunicationFacadeImpl): GuestCommunicationFacade

    @InGameScope
    @Binds
    internal abstract fun provideGuestCommunicator(communicator: GuestCommunicatorImpl): GuestCommunicator

    // ##### GuestCommunicationEventProcessor implementations #####
    @InGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientEvent.CommunicationEvent.ConnectedToHost::class)
    internal abstract fun bindConnectedToHostEventProcessor(
        eventProcessorGuest: GuestConnectedToHostEventProcessor
    ): GuestCommunicationEventProcessor

    @InGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientEvent.CommunicationEvent.DisconnectedFromHost::class)
    internal abstract fun bindDisconnectedFromHostEventProcessor(
        eventProcessorGuest: GuestDisconnectedFromHostEventProcessor
    ): GuestCommunicationEventProcessor

    @InGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientEvent.CommunicationEvent.ConnectionError::class)
    internal abstract fun bindConnectionErrorEventProcessor(
        eventProcessorGuest: GuestConnectionErrorEventProcessor
    ): GuestCommunicationEventProcessor

    @InGameScope
    @Binds
    @IntoMap
    @GuestCommunicationEventProcessorKey(ClientEvent.CommunicationEvent.Stopped::class)
    internal abstract fun bindStoppedEventProcessor(
        eventProcessorGuest: GuestStoppedEventProcessor
    ): GuestCommunicationEventProcessor
}
