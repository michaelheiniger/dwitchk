package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.service.OngoingGameScope
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.carddealer.random.RandomCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetupFactory
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class GameModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameInteractor(gameInteractor: PlayerDashboardFacadeImpl): PlayerDashboardFacade

    @Module
    companion object {

        @OngoingGameScope
        @Provides
        @JvmStatic
        fun provideCardDealerFactory(): CardDealerFactory {
            return RandomCardDealerFactory()
        }

        @OngoingGameScope
        @Provides
        @JvmStatic
        fun provideInitialGameSetupFactory(): InitialGameSetupFactory {
            return RandomInitialGameSetupFactory()
        }
    }
}