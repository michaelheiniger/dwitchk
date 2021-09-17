package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import dagger.Component

@CommunicationScope
@Component(
    modules = [
        CommunicationHostModule::class,
        WebsocketServerModule::class
    ]
)
interface CommunicationHostComponent {
    val commServer: CommServer
    val connectionStore: ConnectionStore

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationHostModule): CommunicationHostComponent
    }
}
