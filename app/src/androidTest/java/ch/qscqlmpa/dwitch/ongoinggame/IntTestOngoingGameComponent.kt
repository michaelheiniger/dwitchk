package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ongoinggame.communication.CommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.IntTestWebsocketModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.game.PlayerDashboardFacade
import ch.qscqlmpa.dwitch.ongoinggame.game.TestGameModule
import ch.qscqlmpa.dwitch.ongoinggame.gameroom.GameRoomGuestFacade
import ch.qscqlmpa.dwitch.ongoinggame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitch.ongoinggame.gameroom.GameRoomModule
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStoreModule
import ch.qscqlmpa.dwitch.ongoinggame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitch.ongoinggame.waitingroom.WaitingRoomHostFacade
import ch.qscqlmpa.dwitch.ongoinggame.waitingroom.WaitingRoomModule
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(modules = [
    OngoingGameModule::class,
    InGameStoreModule::class,
    SerializationModule::class,
    WaitingRoomModule::class,
    GameRoomModule::class,
    TestGameModule::class,
    MessageProcessorModule::class,
    CommunicationModule::class,
    GuestCommunicationEventProcessorModule::class,
    HostCommunicationEventProcessorModule::class,
    GuestCommunicationModule::class,
    HostCommunicationModule::class,
    IntTestWebsocketModule::class
])
interface IntTestOngoingGameComponent {

    val serializerFactory: SerializerFactory

    val websocketClientFactory: WebsocketClientFactory
    val websocketServer: WebsocketServer

    val initialGameSetupFactory: InitialGameSetupFactory
    val cardDealerFactory: CardDealerFactory

    val inGameStore: InGameStore

    val waitingRoomGuestFacade: WaitingRoomGuestFacade
    val waitingRoomHostFacade: WaitingRoomHostFacade
    val gameRoomGuestFacade: GameRoomGuestFacade
    val gameRoomHostFacade: GameRoomHostFacade

    val hostCommunicator: HostCommunicator
    val guestCommunicator: GuestCommunicator

    val playerDashboardFacade: PlayerDashboardFacade
}