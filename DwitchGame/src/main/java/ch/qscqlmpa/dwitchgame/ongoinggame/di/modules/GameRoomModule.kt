package ch.qscqlmpa.dwitchgame.ongoinggame.di.modules

import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacadeImpl
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomHostFacadeImpl
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
}
