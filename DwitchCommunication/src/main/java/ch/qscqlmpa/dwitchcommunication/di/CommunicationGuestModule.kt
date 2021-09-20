package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreImpl
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreInternal
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketCommClient
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
class CommunicationGuestModule(
    private val idlingResource: DwitchIdlingResource
) {
    @InGameCommunicationScope
    @Provides
    internal fun provideCommClient(
        websocketClientFactory: WebsocketClientFactory,
        serializerFactory: SerializerFactory
    ): CommClient {
        return WebsocketCommClient(websocketClientFactory, serializerFactory)
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
