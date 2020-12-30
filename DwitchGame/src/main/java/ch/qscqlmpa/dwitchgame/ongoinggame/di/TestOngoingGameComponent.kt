package ch.qscqlmpa.dwitchgame.ongoinggame.di

import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.di.modules.*
import dagger.Subcomponent

@OngoingGameScope
@Subcomponent(modules = [
    OngoingGameModule::class,
    WaitingRoomModule::class,
    GameRoomModule::class,
    TestGameModule::class,
    MessageProcessorModule::class,
    GuestCommunicationEventProcessorModule::class,
    HostCommunicationEventProcessorModule::class,
    GuestCommunicationModule::class,
    HostCommunicationModule::class,
    GameAdvertisingModule::class,
    DwitchEventRepositoryModule::class
])
interface TestOngoingGameComponent : OngoingGameComponent {
    val initialGameSetupFactory: InitialGameSetupFactory
    val cardDealerFactory: CardDealerFactory
}
