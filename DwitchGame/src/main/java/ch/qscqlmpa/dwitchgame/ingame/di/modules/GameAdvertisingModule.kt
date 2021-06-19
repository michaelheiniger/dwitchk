package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingImpl
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameAdvertisingModule {

    @OngoingGameScope
    @Binds
    internal abstract fun bindGameAdvertising(gameAdvertising: GameAdvertisingImpl): GameAdvertising
}
