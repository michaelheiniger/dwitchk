package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.*
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class WaitingRoomModule {

    @InGameScope
    @Binds
    internal abstract fun provideWaitingRoomGuestFacade(facade: WaitingRoomGuestFacadeImpl): WaitingRoomGuestFacade

    @InGameScope
    @Binds
    internal abstract fun provideWaitingRoomHostFacade(facade: WaitingRoomHostFacadeImpl): WaitingRoomHostFacade

    @InGameScope
    @Binds
    internal abstract fun provideWaitingRoomFacade(facade: WaitingRoomFacadeImpl): WaitingRoomFacade
}
