package ch.qscqlmpa.dwitchgame.ongoinggame.di

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.di.modules.*
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(modules = [
    OngoingGameModule::class,
    WaitingRoomModule::class,
    GameRoomModule::class,
    TestGameInteractorModule::class,
    MessageProcessorModule::class,
    GuestCommunicationEventProcessorModule::class,
    HostCommunicationEventProcessorModule::class,
    GuestCommunicationModule::class,
    HostCommunicationModule::class
])
interface TestOngoingGameComponent : OngoingGameComponent {

    val initialGameSetupFactory: InitialGameSetupFactory
}
