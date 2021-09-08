package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRename
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(
    modules = [
        InGameModule::class,
        InGameHostModule::class,
        InGameGuestModule::class,
        WaitingRoomModule::class,
        GameRoomModule::class,
        DwitchModule::class,
        MessageProcessorModule::class,
        GuestCommunicationModule::class,
        HostCommunicationModule::class,
    ]
)
interface InGameComponent {
    val gameFacadeToRename: GameFacadeToRename
    val hostCommunicationFacade: HostCommunicationFacade
    val guestCommunicationFacade: GuestCommunicationFacade
    val waitingRoomFacade: WaitingRoomFacade
    val waitingRoomHostFacade: WaitingRoomHostFacade
    val waitingRoomGuestFacade: WaitingRoomGuestFacade
    val inGameHostFacade: InGameHostFacade
    val inGameGuestFacade: InGameGuestFacade
    val playerFacade: PlayerFacade
}
