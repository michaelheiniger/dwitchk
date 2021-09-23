package ch.qscqlmpa.dwitch.ingame

import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRename
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import dagger.Module
import dagger.Provides

@Suppress("unused")
@Module
class InGameHostUiModule(
    private val gameFacadeToRename: GameFacadeToRename,
    private val gameAdvertisingFacade: GameAdvertisingFacade,
    private val hostCommunicationFacade: HostCommunicationFacade,
    private val waitingRoomFacade: WaitingRoomFacade,
    private val waitingRoomHostFacade: WaitingRoomHostFacade,
    private val inGameHostFacade: InGameHostFacade,
    private val playerFacade: PlayerFacade
) {

    @Provides
    fun provideGameFacade(): GameFacadeToRename {
        return gameFacadeToRename
    }

    @Provides
    fun provideGameAdvertisingFacade(): GameAdvertisingFacade {
        return gameAdvertisingFacade
    }

    @Provides
    fun provideHostFacade(): HostCommunicationFacade {
        return hostCommunicationFacade
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
    fun provideGameRoomHostFacade(): InGameHostFacade {
        return inGameHostFacade
    }

    @Provides
    fun providePlayerDashboardFacade(): PlayerFacade {
        return playerFacade
    }
}
