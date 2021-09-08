package ch.qscqlmpa.dwitchgame.ingame.di.modules

import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRename
import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRenameImpl
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class GameRoomModule {

    @OngoingGameScope
    @Binds
    internal abstract fun provideGameFacade(facade: GameFacadeToRenameImpl): GameFacadeToRename
}
