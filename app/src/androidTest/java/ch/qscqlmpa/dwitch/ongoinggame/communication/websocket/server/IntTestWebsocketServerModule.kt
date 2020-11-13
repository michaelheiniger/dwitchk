package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server

import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.IntTestWebsocketClientFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class IntTestWebsocketServerModule {

    @Module
    companion object {

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindWebsocketServer(
                @Named(OnGoingGameQualifiers.HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(OnGoingGameQualifiers.HOST_PORT) hostPort: Int
        ): WebsocketServer {
            return IntTestWebsocketServer(hostIpAddress, hostPort)
        }
    }
}