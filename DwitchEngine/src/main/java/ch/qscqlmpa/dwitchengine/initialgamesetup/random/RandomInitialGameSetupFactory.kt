package ch.qscqlmpa.dwitchengine.initialgamesetup.random

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory

class RandomInitialGameSetupFactory : InitialGameSetupFactory {

    override fun getInitialGameSetup(numPlayers: Int): InitialGameSetup {
        return RandomInitialGameSetup(numPlayers)
    }
}