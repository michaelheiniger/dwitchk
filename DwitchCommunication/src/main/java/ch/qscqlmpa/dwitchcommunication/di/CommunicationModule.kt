package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreImpl
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreInternal
import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_IP_ADDRESS
import ch.qscqlmpa.dwitchcommunication.di.Qualifiers.HOST_PORT
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketCommClient
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketCommServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import javax.inject.Named

@Module
class CommunicationModule(
    private val hostIpAddress: String,
    private val hostPort: Int
) {

    @Named(HOST_IP_ADDRESS)
    @Provides
    fun provideHostIpAddress(): String {
        return hostIpAddress
    }

    @Named(HOST_PORT)
    @Provides
    fun provideHostPort(): Int {
        return hostPort
    }

    @CommunicationScope
    @Provides
    internal fun provideCommClient(
        websocketClientFactory: WebsocketClientFactory,
        serializerFactory: SerializerFactory
    ): CommClient {
        return WebsocketCommClient(websocketClientFactory, serializerFactory)
    }

    @CommunicationScope
    @Provides
    internal fun provideWebCommServer(
        websocketServer: WebsocketServer,
        serializerFactory: SerializerFactory,
        connectionStore: ConnectionStoreInternal
    ): CommServer {
        return WebsocketCommServer(websocketServer, serializerFactory, connectionStore)
    }

    @CommunicationScope
    @Provides
    fun provideJsonSerializer(): Json {
        return Json.Default
    }

    @CommunicationScope
    @Provides
    internal fun provideConnectionStoreImpl(): ConnectionStoreImpl {
        return ConnectionStoreImpl()
    }

    @CommunicationScope
    @Provides
    internal fun provideConnectionStoreInternal(connectionStore: ConnectionStoreImpl): ConnectionStoreInternal {
        return connectionStore
    }

    @CommunicationScope
    @Provides
    internal fun provideConnectionStore(connectionStore: ConnectionStoreImpl): ConnectionStore {
        return connectionStore
    }
}

