package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitchgame.ingame.GameFacade
import ch.qscqlmpa.dwitchgame.ingame.common.GuestGameFacade
import ch.qscqlmpa.dwitchgame.ingame.common.HostGameFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class OnGoingGameUiModule(
    private val gameFacade: GameFacade,
    private val hostGameFacade: HostGameFacade,
    private val guestGameFacade: GuestGameFacade,
    private val waitingRoomFacade: WaitingRoomFacade,
    private val waitingRoomHostFacade: WaitingRoomHostFacade,
    private val waitingRoomGuestFacade: WaitingRoomGuestFacade,
    private val gameRoomHostFacade: GameRoomHostFacade,
    private val gameRoomGuestFacade: GameRoomGuestFacade,
    private val playerFacade: PlayerFacade
) {

    @Provides
    fun provideHostFacade(): HostGameFacade {
        return hostGameFacade
    }

    @Provides
    fun provideGuestFacade(): GuestGameFacade {
        return guestGameFacade
    }

    @Provides
    fun provideGameFacade(): GameFacade {
        return gameFacade
    }

    @Provides
    fun provideWaitingRoomFacade(): WaitingRoomFacade {
        return waitingRoomFacade
    }

    @Provides
    fun provideWaitingRoomHostFacade(): WaitingRoomHostFacade {
        return waitingRoomHostFacade
    }

    @Provides
    fun provideWaitingRoomGuestFacade(): WaitingRoomGuestFacade {
        return waitingRoomGuestFacade
    }

    @Provides
    fun provideGameRoomHostFacade(): GameRoomHostFacade {
        return gameRoomHostFacade
    }

    @Provides
    fun provideGameRoomGuestFacade(): GameRoomGuestFacade {
        return gameRoomGuestFacade
    }

    @Provides
    fun providePlayerDashboardFacade(): PlayerFacade {
        return playerFacade
    }
}
