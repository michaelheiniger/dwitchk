package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class TestGameModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameInteractor(gameInteractor: GameFacade): GameFacade

    @Module
    companion object {

        @OngoingGameScope
        @Provides
        @JvmStatic
        internal fun provideCardDealerFactory(): CardDealerFactory {
            return DeterministicCardDealerFactory()
        }

        @OngoingGameScope
        @Provides
        @JvmStatic
        internal fun provideInitialGameSetup(): InitialGameSetupFactory {
            return DeterministicInitialGameSetupFactory()
        }
    }
}
