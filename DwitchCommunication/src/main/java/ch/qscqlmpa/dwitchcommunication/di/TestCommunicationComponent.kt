package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import dagger.Component

@CommunicationScope
@Component(modules = [
    CommunicationModule::class,
    TestWebsocketModule::class
])
interface TestCommunicationComponent : CommunicationComponent {

    val websocketServer: WebsocketServer
    val websocketClientFactory: WebsocketClientFactory
    val serializerFactory: SerializerFactory

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationModule): TestCommunicationComponent
    }
}
