package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacadeImpl
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.LanGameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.udp.UdpNetworkAdapter
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameDiscoveryModule {

    @Binds
    internal abstract fun provideNetworkAdapter(networkListener: UdpNetworkAdapter): NetworkAdapter

    @GameScope
    @Binds
    internal abstract fun provideGameDiscovery(gameDiscovery: LanGameDiscovery): GameDiscovery

    @GameScope
    @Binds
    internal abstract fun provideGameDiscoveryFacade(facade: GameDiscoveryFacadeImpl): GameDiscoveryFacade
}
