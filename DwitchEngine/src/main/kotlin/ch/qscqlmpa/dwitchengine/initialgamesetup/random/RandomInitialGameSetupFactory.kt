package ch.qscqlmpa.dwitchengine.initialgamesetup.random

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

class RandomInitialGameSetupFactory : InitialGameSetupFactory {

    override fun getInitialGameSetup(playersId: Set<DwitchPlayerId>): InitialGameSetup {
        return RandomInitialGameSetup(playersId)
    }
}
