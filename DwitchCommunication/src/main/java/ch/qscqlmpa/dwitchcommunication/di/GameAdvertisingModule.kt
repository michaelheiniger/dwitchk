package ch.qscqlmpa.dwitchcommunication.di

import ch.qscqlmpa.dwitchcommunication.gameadvertising.GameAdvertiser
import ch.qscqlmpa.dwitchcommunication.gameadvertising.GameAdvertiserImpl
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
internal abstract class GameAdvertisingModule {

    @CommunicationScope
    @Binds
    internal abstract fun bindGameAdvertising(gameAdvertising: GameAdvertiserImpl): GameAdvertiser
}
