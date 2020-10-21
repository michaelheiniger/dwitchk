package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ongoinggame.communication.CommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.IntTestWebsocketClientModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.IntTestWebsocketServerModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.game.GameInteractor
import ch.qscqlmpa.dwitch.ongoinggame.game.TestGameModule
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStoreModule
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LaunchGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.PlayerReadyUsecase
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(modules = [
    OngoingGameModule::class,
    InGameStoreModule::class,
    TestGameModule::class,
    MessageProcessorModule::class,
    GuestCommunicationEventProcessorModule::class,
    HostCommunicationEventProcessorModule::class,
    GuestCommunicationModule::class,
    HostCommunicationModule::class,
    CommunicationModule::class,
    IntTestWebsocketClientModule::class,
    IntTestWebsocketServerModule::class
])
interface IntTestOngoingGameComponent {

    val serializerFactory: SerializerFactory

    val websocketClient: WebsocketClient
    val websocketServer: WebsocketServer

    val initialGameSetupFactory: InitialGameSetupFactory

    val inGameStore: InGameStore

    val playerReadyUsecase: PlayerReadyUsecase
    val gameLaunchableUsecase: GameLaunchableUsecase
    val launchGameUsecase: LaunchGameUsecase

    val hostCommunication: HostCommunicator
    val guestCommunication: GuestCommunicator

    val gameInteractor: GameInteractor
}