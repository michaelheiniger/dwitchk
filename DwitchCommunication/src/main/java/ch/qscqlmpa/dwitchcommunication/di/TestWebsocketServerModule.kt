package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.ServerTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.TestWebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.TestWebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.WebsocketServerTestStub
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class TestWebsocketServerModule {

    @InGameCommunicationScope
    @Provides
    internal fun bindTestWebsocketServerFactory(): WebsocketServerFactory {
        return TestWebsocketServerFactory()
    }

    @InGameCommunicationScope
    @Provides
    internal fun bindServerTestStub(
        serverFactory: WebsocketServerFactory,
        serializerFactory: SerializerFactory
    ): ServerTestStub = WebsocketServerTestStub(serverFactory.create("0.0.0.0", 8889) as TestWebsocketServer, serializerFactory)
}
