package ch.qscqlmpa.dwitchgame.di

import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacadeImpl
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.LanGameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.TestNetworkAdapter
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class TestGameDiscoveryModule {

    @GameScope
    @Binds
    internal abstract fun bindGameDiscovery(networkListener: TestNetworkAdapter): NetworkAdapter

    @GameScope
    @Binds
    internal abstract fun bindGameDiscoveryGameDiscovery(gameDiscovery: LanGameDiscovery): GameDiscovery

    @GameScope
    @Binds
    internal abstract fun provideGameDiscoveryFacade(facade: GameDiscoveryFacadeImpl): GameDiscoveryFacade
}
