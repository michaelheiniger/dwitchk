package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchcommunication.ingame.CommServer
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.BindsInstance
import dagger.Subcomponent

@InGameScope
@Subcomponent(
    modules = [
        InGameHostModule::class,
        WaitingRoomModule::class,
        TestDwitchModule::class,
        HostMessageProcessorModule::class,
        HostCommunicationModule::class,
    ]
)
interface TestInGameHostComponent : InGameHostComponent, TestInGameComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance inGameStore: InGameStore,
            @BindsInstance commServer: CommServer,
            @BindsInstance connectionStore: ConnectionStore
        ): TestInGameHostComponent
    }
}
