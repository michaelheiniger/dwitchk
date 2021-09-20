package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.ServerTestStub
import dagger.Component

@CommunicationScope
@Component(
    modules = [
        CommunicationHostModule::class,
        TestWebsocketServerModule::class
    ]
)
interface TestInGameHostCommunicationComponent : InGameHostCommunicationComponent {

    val serverTestStub: ServerTestStub
    val serializerFactory: SerializerFactory

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationHostModule): TestInGameHostCommunicationComponent
    }
}
