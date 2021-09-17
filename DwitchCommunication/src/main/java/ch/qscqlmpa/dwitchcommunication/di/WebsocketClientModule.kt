package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.websocket.client.ProdWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Suppress("unused")
@Module
internal class WebsocketClientModule {

    @CommunicationScope
    @Provides
    internal fun bindWebsocketClient(
        @Named(Qualifiers.HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(Qualifiers.HOST_PORT) hostPort: Int
    ): WebsocketClientFactory {
        return ProdWebsocketClientFactory(hostIpAddress, hostPort)
    }
}
