package ch.qscqlmpa.dwitchgame.di.modules

import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.home.*
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class HomeFacadeModule {

    @GameScope
    @Binds
    internal abstract fun provideHomeFacade(facade: HomeFacadeImpl): HomeFacade

    @GameScope
    @Binds
    internal abstract fun provideHomeGuestFacade(facade: HomeGuestFacadeImpl): HomeGuestFacade

    @GameScope
    @Binds
    internal abstract fun provideHomeHostFacade(facade: HomeHostFacadeImpl): HomeHostFacade
}
