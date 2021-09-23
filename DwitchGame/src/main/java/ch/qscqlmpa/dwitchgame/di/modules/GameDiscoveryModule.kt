package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacadeImpl
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class GameDiscoveryModule(
    private val gameDiscovery: GameDiscovery
) {
    @GameScope
    @Provides
    internal fun provideGameDiscovery(): GameDiscovery {
        return gameDiscovery
    }

    @GameScope
    @Provides
    internal fun provideGameDiscoveryFacade(advertisedGameRepository: AdvertisedGameRepository): GameDiscoveryFacade {
        return GameDiscoveryFacadeImpl(advertisedGameRepository)
    }
}
