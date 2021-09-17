package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_PORT
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.ServerTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.TestWebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.TestWebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.WebsocketServerTestStub
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Suppress("unused")
@Module
class TestWebsocketServerModule {

    @CommunicationScope
    @Provides
    internal fun bindTestWebsocketServerFactory(
        @Named(HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(HOST_PORT) hostPort: Int
    ): WebsocketServerFactory {
        return TestWebsocketServerFactory(hostIpAddress, hostPort)
    }

    @CommunicationScope
    @Provides
    internal fun bindServerTestStub(
        serverFactory: WebsocketServerFactory,
        serializerFactory: SerializerFactory
    ): ServerTestStub = WebsocketServerTestStub(serverFactory.create() as TestWebsocketServer, serializerFactory)
}
