package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.ProdDeviceConnectivityRepository
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class CommunicationModule {

    @CommunicationScope
    @Binds
    internal abstract fun provideDeviceCommunicationRepository(repository: ProdDeviceConnectivityRepository): DeviceConnectivityRepository
}
