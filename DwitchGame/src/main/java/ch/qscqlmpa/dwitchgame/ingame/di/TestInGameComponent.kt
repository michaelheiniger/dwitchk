package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(
    modules = [
        InGameModule::class,
        WaitingRoomModule::class,
        GameRoomModule::class,
        TestGameModule::class,
        MessageProcessorModule::class,
        GuestCommunicationEventProcessorModule::class,
        HostCommunicationEventProcessorModule::class,
        GuestCommunicationModule::class,
        HostCommunicationModule::class,
        GameAdvertisingModule::class
    ]
)
interface TestInGameComponent : InGameComponent {
    val initialGameSetupFactory: InitialGameSetupFactory
    val cardDealerFactory: CardDealerFactory
}
