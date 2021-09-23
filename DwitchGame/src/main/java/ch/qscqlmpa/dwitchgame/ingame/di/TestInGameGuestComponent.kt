package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import dagger.Subcomponent

@InGameScope
@Subcomponent(
    modules = [
        InGameGuestModule::class,
        WaitingRoomModule::class,
        GameRoomModule::class,
        TestDwitchModule::class,
        GuestMessageProcessorModule::class,
        GuestCommunicationModule::class,
    ]
)
interface TestInGameGuestComponent : InGameGuestComponent, TestInGameComponent
