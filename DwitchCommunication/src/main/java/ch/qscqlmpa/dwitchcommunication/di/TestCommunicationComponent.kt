package ch.qscqlmpa.dwitchcommunication.di

import android.net.ConnectivityManager
import ch.qscqlmpa.dwitchcommunication.common.SchedulersModule
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.NetworkAdapter
import dagger.BindsInstance
import dagger.Component

@CommunicationScope
@Component(
    modules = [
        TestCommunicationModule::class,
        GameAdvertisingModule::class,
        TestGameDiscoveryModule::class,
        UtilsModule::class,
        SchedulersModule::class,
    ]
)
interface TestCommunicationComponent : CommunicationComponent {
    val networkListener: NetworkAdapter

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance manager: ConnectivityManager): TestCommunicationComponent
    }
}
