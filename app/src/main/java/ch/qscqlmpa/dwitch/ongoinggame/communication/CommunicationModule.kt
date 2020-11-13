package ch.qscqlmpa.dwitch.ongoinggame.communication

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.CommClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.CommServer
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketCommClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.WebsocketCommServer
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.WebsocketServer
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
            websocketClientFactory: WebsocketClientFactory,
            serializerFactory: SerializerFactory
        ): CommClient {
            return WebsocketCommClient(websocketClientFactory, serializerFactory)
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