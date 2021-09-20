package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.websocket.server.ProdWebsocketServerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServerFactory
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class WebsocketServerModule {

    @InGameCommunicationScope
    @Binds
    internal abstract fun bindWebsocketServer(factory: ProdWebsocketServerFactory): WebsocketServerFactory
}
