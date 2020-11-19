package ch.qscqlmpa.dwitch.ongoinggame.persistence

import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import dagger.Binds
import dagger.Module

@Module
abstract class InGameStoreModule {

    @OngoingGameScope
    @Binds
    abstract fun provideInGameStore(inGameStore: InGameStoreImpl): InGameStore

}