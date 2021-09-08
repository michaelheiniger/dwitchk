package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Binds
import dagger.Module

@Module
abstract class InGameGuestModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideInGameGuestFacade(facade: InGameGuestFacadeImpl): InGameGuestFacade
}