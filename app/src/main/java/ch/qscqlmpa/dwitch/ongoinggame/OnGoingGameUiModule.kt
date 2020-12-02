package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitchgame.ongoinggame.game.GuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.game.HostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.game.PlayerDashboardFacade
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
    private val playerDashboardFacade: PlayerDashboardFacade
) {

    //TODO: Scope ?
    @Provides
    fun provideHostFacade(): HostFacade {
        return hostFacade
    }

    //TODO: Scope ?
    @Provides
    fun provideGuestFacade(): GuestFacade {
        return guestFacade
    }

    //TODO: Scope ?
    @Provides
    fun provideWaitingRoomFacade(): WaitingRoomFacade {
        return waitingRoomFacade
    }

    //TODO: Scope ?
    @Provides
    fun provideWaitingRoomHostFacade(): WaitingRoomHostFacade {
        return waitingRoomHostFacade
    }

    //TODO: Scope ?
    @Provides
    fun provideWaitingRoomGuestFacade(): WaitingRoomGuestFacade {
        return waitingRoomGuestFacade
    }

    //TODO: Scope ?
    @Provides
    fun provideGameRoomHostFacade(): GameRoomHostFacade {
        return gameRoomHostFacade
    }

    //TODO: Scope ?
    @Provides
    fun provideGameRoomGuestFacade(): GameRoomGuestFacade {
        return gameRoomGuestFacade
    }

    //TODO: Scope ?
    @Provides
    fun providePlayerDashboardFacade(): PlayerDashboardFacade {
        return playerDashboardFacade
    }
}