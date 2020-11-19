package ch.qscqlmpa.dwitch.home

import dagger.Binds
import dagger.Module

@Module
abstract class HomeModule {

    @Binds
    internal abstract fun provideGuestFacade(facade: HomeGuestFacadeImpl): HomeGuestFacade

    @Binds
    internal abstract fun provideHostFacade(facade: HomeHostFacadeImpl): HomeHostFacade
}