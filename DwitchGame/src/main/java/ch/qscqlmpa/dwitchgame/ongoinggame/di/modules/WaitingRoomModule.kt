package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.*
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
