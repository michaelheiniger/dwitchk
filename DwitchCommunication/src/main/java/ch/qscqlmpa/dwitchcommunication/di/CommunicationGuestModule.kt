package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.ingame.CommClient
import ch.qscqlmpa.dwitchcommunication.ingame.InGameSerializerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreImpl
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreInternal
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.WebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.WebsocketCommClient
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
        serializerFactory: InGameSerializerFactory
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
