package ch.qscqlmpa.dwitchcommunication.di

import android.content.Context
import ch.qscqlmpa.dwitchcommunication.common.SchedulersModule
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.gameadvertising.GameAdvertiser
import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import dagger.BindsInstance
import dagger.Component

@CommunicationScope
@Component(
    modules = [
        CommunicationModule::class,
        GameAdvertisingModule::class,
        GameDiscoveryModule::class,
        UtilsModule::class,
        SchedulersModule::class,
    ]
)
interface CommunicationComponent {
    val gameDiscovery: GameDiscovery
    val gameAdvertiser: GameAdvertiser
    val deviceConnectivityRepository: DeviceConnectivityRepository

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CommunicationComponent
    }
}
