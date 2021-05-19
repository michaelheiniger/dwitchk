package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.network.LanGameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.network.UdpNetworkAdapter
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameDiscoveryModule {

    @Binds
    internal abstract fun bindNetworkAdapter(networkListener: UdpNetworkAdapter): NetworkAdapter

    @GameScope
    @Binds
    internal abstract fun bindGameDiscoveryGameDiscovery(gameDiscovery: LanGameDiscovery): GameDiscovery
}
