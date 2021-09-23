package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.WLanConnectionRepository
import ch.qscqlmpa.dwitchcommunication.common.SchedulersModule
import ch.qscqlmpa.dwitchcommunication.gameadvertising.GameAdvertiser
import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import dagger.Component

@CommunicationScope
@Component(
    modules = [
        CommunicationModule::class,
        GameAdvertisingModule::class,
        GameDiscoveryModule::class,
        SchedulersModule::class,
    ]
)
interface CommunicationComponent {
    val gameDiscovery: GameDiscovery
    val gameAdvertiser: GameAdvertiser
    val wLanConnectionRepository: WLanConnectionRepository

    @Component.Factory
    interface Factory {
        fun create(communicationModule: CommunicationModule): CommunicationComponent
    }
}
