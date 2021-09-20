package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.ingame.CommServer
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import dagger.Component

@InGameCommunicationScope
@Component(
    modules = [
        CommunicationHostModule::class,
        WebsocketServerModule::class
    ]
)
interface InGameHostCommunicationComponent {
    val commServer: CommServer
    val connectionStore: ConnectionStore

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationHostModule): InGameHostCommunicationComponent
    }
}
