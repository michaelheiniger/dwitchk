package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.ingame.InGameSerializerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.WebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test.ServerTestStub
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test.TestWebsocketServer
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test.TestWebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test.WebsocketServerTestStub
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
        serializerFactory: InGameSerializerFactory
    ): ServerTestStub = WebsocketServerTestStub(serverFactory.create("0.0.0.0", 8889) as TestWebsocketServer, serializerFactory)
}
