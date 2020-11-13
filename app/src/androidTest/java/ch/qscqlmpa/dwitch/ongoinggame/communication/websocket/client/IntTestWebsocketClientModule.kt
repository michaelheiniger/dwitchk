package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.HOST_PORT
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
                @Named(HOST_IP_ADDRESS) hostIpAddress: String,
                @Named(HOST_PORT) hostPort: Int
        ): WebsocketClientFactory {
            return IntTestWebsocketClientFactory(hostIpAddress, hostPort)
        }
    }
}