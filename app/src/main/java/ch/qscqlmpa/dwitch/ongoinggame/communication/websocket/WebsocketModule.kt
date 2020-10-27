package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket

import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_PORT
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.ProdWebsocketClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.ProdWebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitch.service.OngoingGameScope
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
        ): WebsocketClient {
            return ProdWebsocketClient(hostIpAddress, hostPort)
        }
    }
}