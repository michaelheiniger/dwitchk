package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingFacadeImpl
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingImpl
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameAdvertisingModule {

    @GameScope
    @Binds
    internal abstract fun bindGameAdvertising(gameAdvertising: GameAdvertisingImpl): GameAdvertising

    @GameScope
    @Binds
    internal abstract fun provideGameAdvertisingFacade(facade: GameAdvertisingFacadeImpl): GameAdvertisingFacade
}
