package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket

import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_PORT
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.TestWebsocketClientFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.TestWebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
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
                @Named(HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(HOST_PORT) hostPort: Int
        ): WebsocketServer {
            return TestWebsocketServer(hostIpAddress, hostPort)
        }

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindWebsocketClient(
                @Named(HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(HOST_PORT) hostPort: Int
        ): WebsocketClientFactory {
            return TestWebsocketClientFactory(hostIpAddress, hostPort)
        }
    }
}