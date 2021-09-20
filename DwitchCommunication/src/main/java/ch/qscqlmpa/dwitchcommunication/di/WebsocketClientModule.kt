package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.websocket.client.ProdWebsocketClientFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class WebsocketClientModule {

    @CommunicationScope
    @Binds
    internal abstract fun bindWebsocketClient(factory: ProdWebsocketClientFactory): WebsocketClientFactory
}
