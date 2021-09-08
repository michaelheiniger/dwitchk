package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.InInGameHostFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Binds
import dagger.Module

@Module
abstract class InGameHostModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideHostFacade(facade: InInGameHostFacadeImpl): InGameHostFacade
}