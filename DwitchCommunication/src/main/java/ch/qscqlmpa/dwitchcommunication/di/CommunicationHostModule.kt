package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreImpl
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreInternal
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketCommServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServerFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
class CommunicationHostModule(
    private val idlingResource: DwitchIdlingResource
) {
    @InGameCommunicationScope
    @Provides
    internal fun provideCommServer(
        websocketServerFactory: WebsocketServerFactory,
        serializerFactory: SerializerFactory,
        connectionStore: ConnectionStoreInternal
    ): CommServer {
        return WebsocketCommServer(websocketServerFactory, serializerFactory, connectionStore)
    }

    @InGameCommunicationScope
    @Provides
    fun provideJsonSerializer(): Json {
        return Json.Default
    }

    @InGameCommunicationScope
    @Provides
    internal fun provideConnectionStoreImpl(): ConnectionStoreImpl {
        return ConnectionStoreImpl()
    }

    @InGameCommunicationScope
    @Provides
    internal fun provideConnectionStoreInternal(connectionStore: ConnectionStoreImpl): ConnectionStoreInternal {
        return connectionStore
    }

    @InGameCommunicationScope
    @Provides
    internal fun provideConnectionStore(connectionStore: ConnectionStoreImpl): ConnectionStore {
        return connectionStore
    }

    @InGameCommunicationScope
    @Provides
    fun providesIdlingResource(): DwitchIdlingResource {
        return idlingResource
    }
}
