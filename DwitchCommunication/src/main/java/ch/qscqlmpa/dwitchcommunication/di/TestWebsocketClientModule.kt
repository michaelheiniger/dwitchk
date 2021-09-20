package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.TestWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.WebsocketClientTestStub
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class TestWebsocketClientModule {

    @CommunicationScope
    @Provides
    internal fun bindWebsocketClientFactory(): WebsocketClientFactory {
        return TestWebsocketClientFactory()
    }

    @CommunicationScope
    @Provides
    internal fun bindClientTestStub(
        clientFactory: WebsocketClientFactory,
        serializerFactory: SerializerFactory
    ): ClientTestStub = WebsocketClientTestStub(clientFactory as TestWebsocketClientFactory, serializerFactory)
}
