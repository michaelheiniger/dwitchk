package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.ingame.CommClient
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreImpl
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreInternal
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.WebsocketCommClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Suppress("unused")
@Module
abstract class CommunicationGuestModule {

    @InGameCommunicationScope
    @Binds
    internal abstract fun provideCommClient(client: WebsocketCommClient): CommClient

    @InGameCommunicationScope
    @Binds
    internal abstract fun provideConnectionStoreInternal(connectionStore: ConnectionStoreImpl): ConnectionStoreInternal

    @InGameCommunicationScope
    @Binds
    internal abstract fun provideConnectionStore(connectionStore: ConnectionStoreImpl): ConnectionStore

    companion object {
        @InGameCommunicationScope
        @Provides
        fun provideJsonSerializer(): Json {
            return Json.Default
        }
    }
}
