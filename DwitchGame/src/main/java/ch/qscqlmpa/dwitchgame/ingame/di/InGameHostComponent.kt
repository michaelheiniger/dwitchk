package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchcommunication.ingame.CommServer
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.BindsInstance
import dagger.Subcomponent

@InGameScope
@Subcomponent(
    modules = [
        InGameHostModule::class,
        WaitingRoomModule::class,
        DwitchModule::class,
        HostMessageProcessorModule::class,
        HostCommunicationModule::class,
    ]
)
interface InGameHostComponent {
    val gameAdvertisingFacade: GameAdvertisingFacade
    val hostCommunicationFacade: HostCommunicationFacade
    val waitingRoomFacade: WaitingRoomFacade
    val waitingRoomHostFacade: WaitingRoomHostFacade
    val inGameHostFacade: InGameHostFacade
    val playerFacade: PlayerFacade

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance inGameStore: InGameStore,
            @BindsInstance commServer: CommServer,
            @BindsInstance connectionStore: ConnectionStore
        ): InGameHostComponent
    }
}
