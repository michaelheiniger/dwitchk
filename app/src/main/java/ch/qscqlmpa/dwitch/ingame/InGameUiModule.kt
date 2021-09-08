package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRename
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class InGameUiModule(
    private val gameFacadeToRename: GameFacadeToRename,
    private val hostCommunicationFacade: HostCommunicationFacade,
    private val guestCommunicationFacade: GuestCommunicationFacade,
    private val waitingRoomFacade: WaitingRoomFacade,
    private val waitingRoomHostFacade: WaitingRoomHostFacade,
    private val waitingRoomGuestFacade: WaitingRoomGuestFacade,
    private val inGameHostFacade: InGameHostFacade,
    private val inGameGuestFacade: InGameGuestFacade,
    private val playerFacade: PlayerFacade
) {

    @Provides
    fun provideHostFacade(): HostCommunicationFacade {
        return hostCommunicationFacade
    }

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
    fun provideWaitingRoomHostFacade(): WaitingRoomHostFacade {
        return waitingRoomHostFacade
    }

    @Provides
    fun provideWaitingRoomGuestFacade(): WaitingRoomGuestFacade {
        return waitingRoomGuestFacade
    }

    @Provides
    fun provideGameRoomHostFacade(): InGameHostFacade {
        return inGameHostFacade
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
