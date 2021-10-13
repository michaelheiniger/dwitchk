package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacadeImpl
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class GameDiscoveryModule {

    @GameScope
    @Binds
    internal abstract fun provideGameDiscoveryFacade(facade: GameDiscoveryFacadeImpl): GameDiscoveryFacade
}
