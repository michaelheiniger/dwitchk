package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.ingame.InGameSerializerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test.ServerTestStub
import dagger.Component

@InGameCommunicationScope
@Component(
    modules = [
        CommunicationHostModule::class,
        TestWebsocketServerModule::class
    ]
)
interface TestInGameHostCommunicationComponent : InGameHostCommunicationComponent {

    val serverTestStub: ServerTestStub
    val serializerFactory: InGameSerializerFactory

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationHostModule): TestInGameHostCommunicationComponent
    }
}
