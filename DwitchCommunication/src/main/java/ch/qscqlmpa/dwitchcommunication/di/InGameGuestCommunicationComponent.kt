package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import dagger.Component

@CommunicationScope
@Component(
    modules = [
        CommunicationGuestModule::class,
        WebsocketClientModule::class
    ]
)
interface InGameGuestCommunicationComponent {
    val commClient: CommClient
    val connectionStore: ConnectionStore

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationGuestModule): InGameGuestCommunicationComponent
    }
}
