package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacadeImpl
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameLifecycleModule {

    @GameScope
    @Binds
    internal abstract fun provideHomeFacade(facade: GameLifecycleFacadeImpl): GameLifecycleFacade
}
