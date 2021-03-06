package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.ServerTestStub
import dagger.Component
import dagger.Lazy

@CommunicationScope
@Component(
    modules = [
        CommunicationModule::class,
        TestWebsocketModule::class
    ]
)
interface TestCommunicationComponent : CommunicationComponent {

    val serverTestStub: ServerTestStub
    val clientTestStub: Lazy<ClientTestStub>
    val serializerFactory: SerializerFactory

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationModule): TestCommunicationComponent
    }
}
