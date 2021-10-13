package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.game.GameFacade
import ch.qscqlmpa.dwitchgame.game.GameFacadeImpl
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class GameFacadeModule {

    @GameScope
    @Binds
    internal abstract fun provideGameFacade(facade: GameFacadeImpl): GameFacade
}
