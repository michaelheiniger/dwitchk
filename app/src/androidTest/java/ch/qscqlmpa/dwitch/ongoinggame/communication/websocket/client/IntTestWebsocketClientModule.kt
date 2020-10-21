package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class IntTestWebsocketClientModule {

    @Module
    companion object {

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindWebsocketClient(
                @Named(OnGoingGameQualifiers.HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(OnGoingGameQualifiers.HOST_PORT) hostPort: Int
        ): WebsocketClient {
            return IntTestWebsocketClient(hostIpAddress, hostPort)
        }
    }
}