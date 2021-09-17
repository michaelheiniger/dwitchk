package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.websocket.server.ProdWebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServerFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Suppress("unused")
@Module
internal class WebsocketServerModule {

    @CommunicationScope
    @Provides
    internal fun bindWebsocketServer(
        @Named(Qualifiers.HOST_IP_ADDRESS) hostIpAddress: String,
        @Named(Qualifiers.HOST_PORT) hostPort: Int
    ): WebsocketServerFactory {
        return ProdWebsocketServerFactory(hostIpAddress, hostPort)
    }
}
