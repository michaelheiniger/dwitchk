package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.ProdDwitchEngineFactory
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.carddealer.random.RandomCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacadeImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class GameModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameFacade(facade: GameFacadeImpl): GameFacade

    companion object {

        @OngoingGameScope
        @Provides
        fun provideCardDealerFactory(): CardDealerFactory {
            return RandomCardDealerFactory()
        }

        @OngoingGameScope
        @Provides
        fun provideInitialGameSetupFactory(): InitialGameSetupFactory {
            return RandomInitialGameSetupFactory()
        }

        @OngoingGameScope
        @Provides
        internal fun provideDwitchEngineFactory(): DwitchEngineFactory {
            return ProdDwitchEngineFactory()
        }
    }
}
