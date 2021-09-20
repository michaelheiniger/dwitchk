package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.ingame.CommServer
import ch.qscqlmpa.dwitchcommunication.ingame.InGameSerializerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreImpl
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreInternal
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.WebsocketCommServer
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.WebsocketServerFactory
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
        serializerFactory: InGameSerializerFactory,
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
