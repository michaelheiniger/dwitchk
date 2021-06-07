package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchengine.DwitchFactory
import ch.qscqlmpa.dwitchengine.ProdDwitchFactory
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.carddealer.random.RandomCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.PlayerFacadeImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
abstract class GameModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameFacade(facade: PlayerFacadeImpl): PlayerFacade

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
        internal fun provideDwitchFactory(): DwitchFactory {
            return ProdDwitchFactory()
        }
    }
}
