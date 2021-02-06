package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.game.HostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import dagger.Module
import dagger.Provides

@Module
class OnGoingGameUiModule(
    private val hostFacade: HostFacade,
    private val guestFacade: GuestFacade,
    private val waitingRoomFacade: WaitingRoomFacade,
    private val waitingRoomHostFacade: WaitingRoomHostFacade,
    private val waitingRoomGuestFacade: WaitingRoomGuestFacade,
    private val gameRoomHostFacade: GameRoomHostFacade,
    private val gameRoomGuestFacade: GameRoomGuestFacade,
    private val playerDashboardFacade: GameDashboardFacade
) {

    @Provides
    fun provideHostFacade(): HostFacade {
        return hostFacade
    }

    @Provides
    fun provideGuestFacade(): GuestFacade {
        return guestFacade
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
    fun providePlayerDashboardFacade(): GameDashboardFacade {
        return playerDashboardFacade
    }
}
