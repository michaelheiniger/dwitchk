package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.common.SchedulersModule
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.NetworkAdapter
import dagger.Component

@CommunicationScope
@Component(
    modules = [
        CommunicationModule::class,
        GameAdvertisingModule::class,
        TestGameDiscoveryModule::class,
        SchedulersModule::class,
    ]
)
interface TestCommunicationComponent : CommunicationComponent {
    val networkListener: NetworkAdapter

    @Component.Factory
    interface Factory {
        fun create(module: CommunicationModule): TestCommunicationComponent
    }
}
