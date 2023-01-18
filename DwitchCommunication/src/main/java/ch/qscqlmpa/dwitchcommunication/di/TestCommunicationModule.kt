package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.TestDeviceConnectivityRepository
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class TestCommunicationModule {

    @CommunicationScope
    @Binds
    internal abstract fun provideDeviceCommunicationRepository(repository: TestDeviceConnectivityRepository): DeviceConnectivityRepository
}
