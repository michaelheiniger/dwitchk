package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitchgame.ongoinggame.common.GuestGameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.common.HostGameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class OnGoingGameUiModule(
    private val hostGameFacade: HostGameFacade,
    private val guestGameFacade: GuestGameFacade,
    private val waitingRoomFacade: WaitingRoomFacade,
    private val waitingRoomHostFacade: WaitingRoomHostFacade,
    private val waitingRoomGuestFacade: WaitingRoomGuestFacade,
    private val gameRoomHostFacade: GameRoomHostFacade,
    private val gameRoomGuestFacade: GameRoomGuestFacade,
    private val playerFacade: GameFacade
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
    fun providePlayerDashboardFacade(): GameFacade {
        return playerFacade
    }
}
