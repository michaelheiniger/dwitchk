package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules


import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.game.PlayerDashboardFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.game.PlayerDashboardFacadeImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class TestGameInteractorModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameInteractor(gameInteractor: PlayerDashboardFacadeImpl): PlayerDashboardFacade

    companion object {

        @OngoingGameScope
        @Provides
        @JvmStatic
        fun provideCardDealerFactory(): CardDealerFactory {
            return DeterministicCardDealerFactory()
        }

        @OngoingGameScope
        @Provides
        @JvmStatic
        fun provideInitialGameSetupFactory(): InitialGameSetupFactory {
            return DeterministicInitialGameSetupFactory()
        }
    }
}