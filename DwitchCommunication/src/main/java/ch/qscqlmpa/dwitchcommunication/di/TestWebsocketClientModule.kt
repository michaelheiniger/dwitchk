package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.ingame.InGameSerializerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test.TestWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test.WebsocketClientTestStub
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class TestWebsocketClientModule {

    @InGameCommunicationScope
    @Provides
    internal fun bindWebsocketClientFactory(): WebsocketClientFactory {
        return TestWebsocketClientFactory()
    }

    @InGameCommunicationScope
    @Provides
    internal fun bindClientTestStub(
        clientFactory: WebsocketClientFactory,
        serializerFactory: InGameSerializerFactory
    ): ClientTestStub = WebsocketClientTestStub(clientFactory as TestWebsocketClientFactory, serializerFactory)
}
