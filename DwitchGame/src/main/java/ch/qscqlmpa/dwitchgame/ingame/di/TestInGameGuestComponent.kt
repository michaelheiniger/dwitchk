package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.ingame.CommClient
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Named

@InGameScope
@Subcomponent(
    modules = [
        InGameGuestModule::class,
        WaitingRoomModule::class,
        TestDwitchModule::class,
        GuestMessageProcessorModule::class,
        GuestCommunicationModule::class,
    ]
)
interface TestInGameGuestComponent : InGameGuestComponent, TestInGameComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance @Named(InstanceQualifiers.ADVERTISED_GAME) advertisedGame: GameAdvertisingInfo,
            @BindsInstance inGameStore: InGameStore,
            @BindsInstance commClient: CommClient,
            @BindsInstance connectionStore: ConnectionStore
        ): TestInGameGuestComponent
    }
}
