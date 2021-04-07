package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.ProdDwitchEngineFactory
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacadeImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class TestGameModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameDashbordFacade(facade: GameFacadeImpl): GameFacade

    companion object {

        @OngoingGameScope
        @Provides
        fun provideCardDealerFactory(): CardDealerFactory {
            return DeterministicCardDealerFactory()
        }

        @OngoingGameScope
        @Provides
        fun provideInitialGameSetupFactory(): InitialGameSetupFactory {
            return DeterministicInitialGameSetupFactory()
        }

        @OngoingGameScope
        @Provides
        internal fun provideDwitchEngineFactory(): DwitchEngineFactory {
            return ProdDwitchEngineFactory()
        }
    }
}
