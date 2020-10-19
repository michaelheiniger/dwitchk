package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Binds
import dagger.Module

@Module
abstract class InGameStoreModule {

    @OngoingGameScope
    @Binds
    abstract fun provideInGameStore(inGameStore: InGameStoreImpl): InGameStore

}