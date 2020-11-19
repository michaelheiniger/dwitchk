package ch.qscqlmpa.dwitch.ongoinggame.gameroom

import ch.qscqlmpa.dwitch.service.OngoingGameScope
import dagger.Binds
import dagger.Module

@Module
abstract class GameRoomModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGuestFacade(facade: GameRoomGuestFacadeImpl): GameRoomGuestFacade

    @OngoingGameScope
    @Binds
    internal abstract fun provideHostFacade(facade: GameRoomHostFacadeImpl): GameRoomHostFacade

    @OngoingGameScope
    @Binds
    internal abstract fun provideCommonFacade(facade: GameRoomFacadeImpl): GameRoomFacade
}