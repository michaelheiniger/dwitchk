package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_PORT
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.TestWebsocketClient
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.TestWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.WebsocketClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.ServerTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.TestWebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.TestWebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.WebsocketServerTestStub
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class TestWebsocketModule {

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
    internal fun bindWebsocketClientFactory(
        @Named(HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(HOST_PORT) hostPort: Int,
        idlingResource: DwitchIdlingResource
    ): WebsocketClientFactory {
        return TestWebsocketClientFactory(hostIpAddress, hostPort, idlingResource)
    }

    @CommunicationScope
    @Provides
    internal fun bindServerTestStub(
        serverFactory: WebsocketServerFactory,
        serializerFactory: SerializerFactory,
        idlingResource: DwitchIdlingResource
    ): ServerTestStub {
        return WebsocketServerTestStub(serverFactory.create() as TestWebsocketServer, serializerFactory, idlingResource)
    }

    @CommunicationScope
    @Provides
    internal fun bindClientTestStub(
        clientFactory: WebsocketClientFactory,
        serializerFactory: SerializerFactory,
        idlingResource: DwitchIdlingResource
    ): ClientTestStub {
        val client = (clientFactory as TestWebsocketClientFactory).getInstance()
        return WebsocketClientTestStub(client as TestWebsocketClient, serializerFactory, idlingResource)
    }
}
