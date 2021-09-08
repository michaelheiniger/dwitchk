package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ingame.di.modules.*
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(
    modules = [
        InGameModule::class,
        InGameHostModule::class,
        InGameGuestModule::class,
        WaitingRoomModule::class,
        GameRoomModule::class,
        TestDwitchModule::class,
        MessageProcessorModule::class,
        GuestCommunicationModule::class,
        HostCommunicationModule::class,
    ]
)
interface TestInGameComponent : InGameComponent {
    val initialGameSetupFactory: InitialGameSetupFactory
    val cardDealerFactory: CardDealerFactory
}
