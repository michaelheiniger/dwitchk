package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchgame.ingame.GameFacade
import ch.qscqlmpa.dwitchgame.ingame.common.GuestGameFacade
import ch.qscqlmpa.dwitchgame.ingame.common.HostGameFacade
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import ch.qscqlmpa.dwitchgame.ingame.di.modules.GuestCommunicationModule
import ch.qscqlmpa.dwitchgame.ingame.di.modules.HostCommunicationModule
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(
    modules = [
        InGameModule::class,
        WaitingRoomModule::class,
        GameRoomModule::class,
        GameModule::class,
        MessageProcessorModule::class,
        GuestCommunicationEventProcessorModule::class,
        HostCommunicationEventProcessorModule::class,
        GuestCommunicationModule::class,
        HostCommunicationModule::class,
        GameAdvertisingModule::class
    ]
)
interface InGameComponent {
    val gameFacade: GameFacade
    val hostGameFacade: HostGameFacade
    val guestGameFacade: GuestGameFacade
    val waitingRoomFacade: WaitingRoomFacade
    val waitingRoomHostFacade: WaitingRoomHostFacade
    val waitingRoomGuestFacade: WaitingRoomGuestFacade
    val gameRoomHostFacade: GameRoomHostFacade
    val gameRoomGuestFacade: GameRoomGuestFacade
    val playerFacade: PlayerFacade
}
