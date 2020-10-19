package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.ongoinggame.game.GameInteractor
import ch.qscqlmpa.dwitch.ongoinggame.game.GameInteractorImpl
import ch.qscqlmpa.dwitch.ongoinggame.game.RandomCardDealerFactory
import ch.qscqlmpa.dwitch.ongoinggame.game.RandomInitialGameSetupFactory
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import ch.qscqlmpa.dwitchengine.CardDealerFactory
import ch.qscqlmpa.dwitchengine.InitialGameSetupFactory
import dagger.Binds
import dagger.Module

@Module
abstract class GameModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameInteractor(gameInteractor: GameInteractorImpl): GameInteractor

    @OngoingGameScope
    @Binds
    internal abstract fun provideInitialGameSetupFactory(initialGameSetupFactory: RandomInitialGameSetupFactory): InitialGameSetupFactory

    @OngoingGameScope
    @Binds
    internal abstract fun provideCardDealerFactory(cardDealerFactory: RandomCardDealerFactory): CardDealerFactory
}