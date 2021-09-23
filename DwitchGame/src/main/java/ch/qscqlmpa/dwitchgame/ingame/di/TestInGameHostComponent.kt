package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import dagger.Subcomponent

@InGameScope
@Subcomponent(
    modules = [
        InGameHostModule::class,
        WaitingRoomModule::class,
        GameRoomModule::class,
        TestDwitchModule::class,
        HostMessageProcessorModule::class,
        HostCommunicationModule::class,
    ]
)
interface TestInGameHostComponent : InGameHostComponent, TestInGameComponent
