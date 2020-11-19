package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket

import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_PORT
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.ProdWebsocketClientFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.ProdWebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.WebsocketServer
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class WebsocketModule {

    @Module
    companion object {

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindWebsocketServer(
                @Named(HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(HOST_PORT) hostPort: Int
        ): WebsocketServer {
            return ProdWebsocketServer(hostIpAddress, hostPort)
        }

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindWebsocketClient(
                @Named(HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(HOST_PORT) hostPort: Int
        ): WebsocketClientFactory {
            return ProdWebsocketClientFactory(hostIpAddress, hostPort)
        }
    }
}