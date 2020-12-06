package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_PORT
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.*
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.TestWebsocketClient
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.TestWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.WebsocketClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.ServerTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.TestWebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.WebsocketServerTestStub
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class TestWebsocketModule {

    @CommunicationScope
    @Provides
    internal fun bindTestWebsocketServer(
        @Named(HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(HOST_PORT) hostPort: Int
    ): TestWebsocketServer {
        return TestWebsocketServer(hostIpAddress, hostPort)
    }

    @CommunicationScope
    @Provides
    internal fun bindWebsocketServer(server: TestWebsocketServer): WebsocketServer {
        return server
    }

    @CommunicationScope
    @Provides
    internal fun bindTestWebsocketClientFactory(
        @Named(HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(HOST_PORT) hostPort: Int
    ): TestWebsocketClientFactory {
        return TestWebsocketClientFactory(hostIpAddress, hostPort)
    }

    @CommunicationScope
    @Provides
    internal fun bindWebsocketClientFactory(clientFactory: TestWebsocketClientFactory): WebsocketClientFactory {
        return clientFactory
    }

    @CommunicationScope
    @Provides
    internal fun bindServerTestStub(server: TestWebsocketServer, serializerFactory: SerializerFactory): ServerTestStub {
        return WebsocketServerTestStub(server, serializerFactory)
    }

    @CommunicationScope
    @Provides
    internal fun bindClientTestStub(clientFactory: TestWebsocketClientFactory, serializerFactory: SerializerFactory): ClientTestStub {
        return WebsocketClientTestStub(clientFactory.create() as TestWebsocketClient, serializerFactory)
    }
}