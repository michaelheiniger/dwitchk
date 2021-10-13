package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchgame.di.GameScope
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class DeviceConnectivityModule(
    private val deviceConnectivityRepository: DeviceConnectivityRepository
) {
    @GameScope
    @Provides
    internal fun provideDeviceConnectivityRepository(): DeviceConnectivityRepository {
        return deviceConnectivityRepository
    }
}
