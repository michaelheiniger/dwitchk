package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import dagger.Binds
import dagger.Module

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