package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.LanGameDiscovery
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.NetworkAdapter
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.TestNetworkAdapter
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class TestGameDiscoveryModule {

    @CommunicationScope
    @Binds
    internal abstract fun provideNetworkAdapter(networkListener: TestNetworkAdapter): NetworkAdapter

    @CommunicationScope
    @Binds
    internal abstract fun provideGameDiscovery(gameDiscovery: LanGameDiscovery): GameDiscovery
}
