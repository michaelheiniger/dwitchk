package ch.qscqlmpa.dwitch.ongoinggame.communication

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.CommClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.CommServer
import ch.qscqlmpa.dwitch.communication.websocket.client.WebsocketClient
import ch.qscqlmpa.dwitch.communication.websocket.client.WebsocketCommClient
import ch.qscqlmpa.dwitch.communication.websocket.server.WebsocketCommServer
import ch.qscqlmpa.dwitch.communication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Module
import dagger.Provides

@Module
abstract class CommunicationModule {

    @Module
    companion object {

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindCommClient(
                websocketClient: WebsocketClient,
                serializerFactory: SerializerFactory
        ): CommClient {
            return WebsocketCommClient(websocketClient, serializerFactory)
        }

        @OngoingGameScope
        @JvmStatic
        @Provides
        fun bindWebCommServer(websocketServer: WebsocketServer,
                              serializerFactory: SerializerFactory,
                              localConnectionIdStore: LocalConnectionIdStore
        ): CommServer {
            return WebsocketCommServer(websocketServer, serializerFactory, localConnectionIdStore)
        }
    }
}