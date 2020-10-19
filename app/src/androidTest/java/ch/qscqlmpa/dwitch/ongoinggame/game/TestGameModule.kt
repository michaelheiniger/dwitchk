package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.components.game.TestInitialGameSetupFactory
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import ch.qscqlmpa.dwitchengine.CardDealerFactory
import ch.qscqlmpa.dwitchengine.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.TestCardDealerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class TestGameModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameInteractor(gameInteractor: GameInteractorImpl): GameInteractor

    @OngoingGameScope
    @Binds
    internal abstract fun provideInitialGameSetup(initialGameSetupFactory: TestInitialGameSetupFactory): InitialGameSetupFactory

    @Module
    companion object {

        // Triggers error when using @Binds instead of @Provides
        @OngoingGameScope
        @Provides
        @JvmStatic
        internal fun provideCardDealerFactory(): CardDealerFactory {
            return TestCardDealerFactory()
        }
    }
}