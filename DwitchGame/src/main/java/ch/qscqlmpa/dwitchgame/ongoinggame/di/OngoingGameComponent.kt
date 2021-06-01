package ch.qscqlmpa.dwitchgame.ongoinggame.di

import ch.qscqlmpa.dwitchgame.ongoinggame.common.GuestGameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.common.HostGameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.di.modules.*
import ch.qscqlmpa.dwitchgame.ongoinggame.di.modules.GuestCommunicationModule
import ch.qscqlmpa.dwitchgame.ongoinggame.di.modules.HostCommunicationModule
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(
    modules = [
        OngoingGameModule::class,
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
interface OngoingGameComponent {
    val hostGameFacade: HostGameFacade
    val guestGameFacade: GuestGameFacade
    val waitingRoomFacade: WaitingRoomFacade
    val waitingRoomHostFacade: WaitingRoomHostFacade
    val waitingRoomGuestFacade: WaitingRoomGuestFacade
    val gameRoomHostFacade: GameRoomHostFacade
    val gameRoomGuestFacade: GameRoomGuestFacade
    val gameFacade: GameFacade
}
