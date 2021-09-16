package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRename
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(
    modules = [
        InGameGuestModule::class,
        WaitingRoomModule::class,
        GameRoomModule::class,
        DwitchModule::class,
        GuestMessageProcessorModule::class,
        GuestCommunicationModule::class
    ]
)
interface InGameGuestComponent {
    val gameFacadeToRename: GameFacadeToRename
    val guestCommunicationFacade: GuestCommunicationFacade
    val waitingRoomFacade: WaitingRoomFacade
    val waitingRoomGuestFacade: WaitingRoomGuestFacade
    val inGameGuestFacade: InGameGuestFacade
    val playerFacade: PlayerFacade
}
