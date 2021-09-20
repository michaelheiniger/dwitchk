package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.ingame.CommClient
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import dagger.Component

@InGameCommunicationScope
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
