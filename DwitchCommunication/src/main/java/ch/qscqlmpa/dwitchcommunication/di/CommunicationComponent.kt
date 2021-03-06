package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import dagger.Component

@CommunicationScope
@Component(
    modules = [
        CommunicationModule::class,
        WebsocketModule::class
    ]
)
interface CommunicationComponent {
    val commServer: CommServer
    val commClient: CommClient
    val connectionStore: ConnectionStore

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationModule): CommunicationComponent
    }
}
