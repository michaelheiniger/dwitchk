package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.network.LanGameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.network.TestNetworkAdapter
import dagger.Binds
import dagger.Module

@Module
abstract class TestGameDiscoveryModule {

    @GameScope
    @Binds
    internal abstract fun bindGameDiscovery(networkListener: TestNetworkAdapter): NetworkAdapter

    @GameScope
    @Binds
    internal abstract fun bindGameDiscoveryGameDiscovery(gameDiscovery: LanGameDiscovery): GameDiscovery
}
