package ch.qscqlmpa.dwitch.ingame.game

import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
abstract class TestGameModule {

    @InGameScope
    @Binds
    internal abstract fun provideGameInteractor(playerInteractor: PlayerFacade): PlayerFacade

    companion object {

        @InGameScope
        @Provides
        internal fun provideCardDealerFactory(): CardDealerFactory {
            return DeterministicCardDealerFactory()
        }

        @InGameScope
        @Provides
        internal fun provideInitialGameSetup(): InitialGameSetupFactory {
            return DeterministicInitialGameSetupFactory()
        }
    }
}
