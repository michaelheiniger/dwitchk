package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.ingame.InGameSerializerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test.ClientTestStub
import dagger.Component
import dagger.Lazy

@InGameCommunicationScope
@Component(
    modules = [
        CommunicationGuestModule::class,
        TestWebsocketClientModule::class
    ]
)
interface TestInGameGuestCommunicationComponent : InGameGuestCommunicationComponent {

    val clientTestStub: Lazy<ClientTestStub>
    val serializerFactory: InGameSerializerFactory

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationGuestModule): TestInGameGuestCommunicationComponent
    }
}
