package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.websocket.client.ProdWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.ProdWebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
internal class WebsocketModule {

    @CommunicationScope
    @Provides
    internal fun bindWebsocketServer(
        @Named(Qualifiers.HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(Qualifiers.HOST_PORT) hostPort: Int
    ): WebsocketServer {
        return ProdWebsocketServer(hostIpAddress, hostPort)
    }

    @CommunicationScope
    @Provides
    internal fun bindWebsocketClient(
        @Named(Qualifiers.HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(Qualifiers.HOST_PORT) hostPort: Int
    ): WebsocketClientFactory {
        return ProdWebsocketClientFactory(hostIpAddress, hostPort)
    }
}