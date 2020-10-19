package ch.qscqlmpa.dwitch.communication

import ch.qscqlmpa.dwitch.communication.client.websocket.TestWebsocketClient
import ch.qscqlmpa.dwitch.communication.server.websocket.TestWebsocketServer
import ch.qscqlmpa.dwitch.communication.websocket.client.WebsocketClient
import ch.qscqlmpa.dwitch.communication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class TestWebsocketModule {

    @Module
    companion object {

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindWebsocketServer(
                @Named(OnGoingGameQualifiers.HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(OnGoingGameQualifiers.HOST_PORT) hostPort: Int
        ): WebsocketServer {
            return TestWebsocketServer(hostIpAddress, hostPort)
        }

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindWebsocketClient(
                @Named(OnGoingGameQualifiers.HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(OnGoingGameQualifiers.HOST_PORT) hostPort: Int
        ): WebsocketClient {
            return TestWebsocketClient(hostIpAddress, hostPort)
        }
    }

}