package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.ClientTestStub
import dagger.Component
import dagger.Lazy

@CommunicationScope
@Component(
    modules = [
        CommunicationGuestModule::class,
        TestWebsocketClientModule::class
    ]
)
interface TestInGameGuestCommunicationComponent : InGameGuestCommunicationComponent {

    val clientTestStub: Lazy<ClientTestStub>
    val serializerFactory: SerializerFactory

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationGuestModule): TestInGameGuestCommunicationComponent
    }
}
