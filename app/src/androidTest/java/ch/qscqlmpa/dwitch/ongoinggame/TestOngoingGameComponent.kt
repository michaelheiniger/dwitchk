package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.communication.TestWebsocketModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.communication.websocket.client.WebsocketClient
import ch.qscqlmpa.dwitch.communication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitch.components.game.TestGameModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.CommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventProcessorModule
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageProcessorModule
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameScreenBindingModule
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameViewModelBindingModule
import ch.qscqlmpa.dwitchengine.InitialGameSetupFactory
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(modules = [
    OngoingGameModule::class,
    InGameStoreModule::class,
    OngoingGameScreenBindingModule::class,
    OngoingGameViewModelBindingModule::class,
    TestGameModule::class,
    MessageProcessorModule::class,
    GuestCommunicationEventProcessorModule::class,
    HostCommunicationEventProcessorModule::class,
    GuestCommunicationModule::class,
    HostCommunicationModule::class,
    CommunicationModule::class,
    TestWebsocketModule::class
])
interface TestOngoingGameComponent : OngoingGameComponent {

    val serializerFactory: SerializerFactory

    val websocketClient: WebsocketClient
    val websocketServer: WebsocketServer

    val initialGameSetupFactory: InitialGameSetupFactory

    val inGameStore: InGameStore
}
