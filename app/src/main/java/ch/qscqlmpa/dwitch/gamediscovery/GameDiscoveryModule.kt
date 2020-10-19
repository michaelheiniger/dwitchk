package ch.qscqlmpa.dwitch.gamediscovery


import ch.qscqlmpa.dwitch.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitch.gamediscovery.network.LanGameDiscovery
import ch.qscqlmpa.dwitch.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitch.gamediscovery.network.UdpNetworkAdapter
import dagger.Binds
import dagger.Module

@Module
abstract class GameDiscoveryModule {

    @Binds
    internal abstract fun bindNetworkAdapter(networkListener: UdpNetworkAdapter): NetworkAdapter

    @Binds
    internal abstract fun bindGameDiscoveryGameDiscovery(gameDiscovery: LanGameDiscovery): GameDiscovery
}
