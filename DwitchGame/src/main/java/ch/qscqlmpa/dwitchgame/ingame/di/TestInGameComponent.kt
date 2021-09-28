package ch.qscqlmpa.dwitchgame.ingame.di

import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory

interface TestInGameComponent {
    val initialGameSetupFactory: InitialGameSetupFactory
    val cardDealerFactory: CardDealerFactory
}
