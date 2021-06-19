package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.ingame.GameFacade
import ch.qscqlmpa.dwitchgame.ingame.GameFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomGuestFacadeImpl
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomHostFacadeImpl
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameRoomModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameFacade(facade: GameFacadeImpl): GameFacade

    @OngoingGameScope
    @Binds
    internal abstract fun provideGuestFacade(facade: GameRoomGuestFacadeImpl): GameRoomGuestFacade

    @OngoingGameScope
    @Binds
    internal abstract fun provideHostFacade(facade: GameRoomHostFacadeImpl): GameRoomHostFacade
}
