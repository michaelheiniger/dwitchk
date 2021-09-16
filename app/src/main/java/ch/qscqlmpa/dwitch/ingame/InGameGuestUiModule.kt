package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRename
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class InGameGuestUiModule(
    private val gameFacadeToRename: GameFacadeToRename,
    private val guestCommunicationFacade: GuestCommunicationFacade,
    private val waitingRoomFacade: WaitingRoomFacade,
    private val waitingRoomGuestFacade: WaitingRoomGuestFacade,
    private val inGameGuestFacade: InGameGuestFacade,
    private val playerFacade: PlayerFacade
) {

    @Provides
    fun provideGuestFacade(): GuestCommunicationFacade {
        return guestCommunicationFacade
    }

    @Provides
    fun provideGameFacade(): GameFacadeToRename {
        return gameFacadeToRename
    }

    @Provides
    fun provideWaitingRoomFacade(): WaitingRoomFacade {
        return waitingRoomFacade
    }

    @Provides
    fun provideWaitingRoomGuestFacade(): WaitingRoomGuestFacade {
        return waitingRoomGuestFacade
    }

    @Provides
    fun provideGameRoomGuestFacade(): InGameGuestFacade {
        return inGameGuestFacade
    }

    @Provides
    fun providePlayerDashboardFacade(): PlayerFacade {
        return playerFacade
    }
}
