package ch.qscqlmpa.dwitch.gamediscovery


import ch.qscqlmpa.dwitch.gamediscovery.network.LanGameDiscovery
import ch.qscqlmpa.dwitch.gamediscovery.network.NetworkAdapter
import dagger.Binds
import dagger.Module

@Module
abstract class TestGameDiscoveryModule {

    @Binds
    internal abstract fun bindGameDiscovery(networkListener: TestNetworkAdapter): NetworkAdapter

    @Binds
    internal abstract fun bindGameDiscoveryGameDiscovery(gameDiscovery: LanGameDiscovery): GameDiscovery
}
