package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_PORT
import ch.qscqlmpa.dwitchcommunication.websocket.client.TestWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.TestWebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class TestWebsocketModule {

    @CommunicationScope
    @Provides
    internal fun bindWebsocketServer(): WebsocketServer {
        return TestWebsocketServer()
    }

    @CommunicationScope
    @Provides
    internal fun bindWebsocketClient(
        @Named(HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(HOST_PORT) hostPort: Int
    ): WebsocketClientFactory {
        return TestWebsocketClientFactory(hostIpAddress, hostPort)
    }
}