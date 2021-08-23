package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.common.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.common.GameAdvertisingFacadeImpl
import ch.qscqlmpa.dwitchgame.di.GameScope
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameAdvertisingFacadeModule {

    @GameScope
    @Binds
    internal abstract fun provideGameAdvertisingFacade(facade: GameAdvertisingFacadeImpl): GameAdvertisingFacade
}
