package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchengine.DwitchFactory
import ch.qscqlmpa.dwitchengine.ProdDwitchFactory
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.computerplayer.ComputerReflexionTime
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacadeImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
abstract class TestDwitchModule {

    @InGameScope
    @Binds
    internal abstract fun provideGameDashbordFacade(facade: PlayerFacadeImpl): PlayerFacade

    companion object {

        @InGameScope
        @Provides
        fun provideCardDealerFactory(): CardDealerFactory {
            return DeterministicCardDealerFactory()
        }

        @InGameScope
        @Provides
        fun provideInitialGameSetupFactory(): InitialGameSetupFactory {
            return DeterministicInitialGameSetupFactory()
        }

        @InGameScope
        @Provides
        internal fun provideDwitchEngineFactory(): DwitchFactory {
            return ProdDwitchFactory(ComputerReflexionTime.ZERO)
        }
    }
}
