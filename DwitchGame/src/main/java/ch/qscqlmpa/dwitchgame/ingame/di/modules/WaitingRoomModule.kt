package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.*
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class WaitingRoomModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideWaitingRoomGuestFacade(facade: WaitingRoomGuestFacadeImpl): WaitingRoomGuestFacade

    @OngoingGameScope
    @Binds
    internal abstract fun provideWaitingRoomHostFacade(facade: WaitingRoomHostFacadeImpl): WaitingRoomHostFacade

    @OngoingGameScope
    @Binds
    internal abstract fun provideWaitingRoomFacade(facade: WaitingRoomFacadeImpl): WaitingRoomFacade
}
