package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRename
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import dagger.Subcomponent

@InGameScope
@Subcomponent(
    modules = [
        InGameHostModule::class,
        WaitingRoomModule::class,
        GameRoomModule::class,
        DwitchModule::class,
        HostMessageProcessorModule::class,
        HostCommunicationModule::class,
    ]
)
interface InGameHostComponent {
    val gameAdvertisingFacade: GameAdvertisingFacade
    val gameFacadeToRename: GameFacadeToRename
    val hostCommunicationFacade: HostCommunicationFacade
    val waitingRoomFacade: WaitingRoomFacade
    val waitingRoomHostFacade: WaitingRoomHostFacade
    val inGameHostFacade: InGameHostFacade
    val playerFacade: PlayerFacade
}
