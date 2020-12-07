package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacadeImpl
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacadeImpl
import dagger.Binds
import dagger.Module

@Module
abstract class HomeModule {

    @GameScope
    @Binds
    internal abstract fun provideGuestFacade(facade: HomeGuestFacadeImpl): HomeGuestFacade

    @GameScope
    @Binds
    internal abstract fun provideHostFacade(facade: HomeHostFacadeImpl): HomeHostFacade
}