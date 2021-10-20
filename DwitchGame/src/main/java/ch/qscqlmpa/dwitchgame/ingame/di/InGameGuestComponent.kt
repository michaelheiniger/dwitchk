package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.ingame.CommClient
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@InGameScope
@Subcomponent(
    modules = [
        InGameGuestModule::class,
        WaitingRoomModule::class,
        DwitchModule::class,
        GuestMessageProcessorModule::class,
        GuestCommunicationModule::class
    ]
)
interface InGameGuestComponent {
    val guestCommunicationFacade: GuestCommunicationFacade
    val waitingRoomFacade: WaitingRoomFacade
    val waitingRoomGuestFacade: WaitingRoomGuestFacade
    val inGameGuestFacade: InGameGuestFacade
    val playerFacade: PlayerFacade

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance @Named(InstanceQualifiers.ADVERTISED_GAME) advertisedGame: GameAdvertisingInfo,
            @BindsInstance inGameStore: InGameStore,
            @BindsInstance commClient: CommClient,
            @BindsInstance connectionStore: ConnectionStore
        ): InGameGuestComponent
    }
}
