package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.LanGameDiscovery
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.NetworkAdapter
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.udp.UdpNetworkAdapter
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameDiscoveryModule {

    @Binds
    internal abstract fun provideNetworkAdapter(networkListener: UdpNetworkAdapter): NetworkAdapter

    @CommunicationScope
    @Binds
    internal abstract fun provideGameDiscovery(gameDiscovery: LanGameDiscovery): GameDiscovery
}
