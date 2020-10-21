package ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory

class DeterministicInitialGameSetupFactory : InitialGameSetupFactory {

    override fun getInitialGameSetup(numPlayers: Int): InitialGameSetup {
        if (instance == null) {
            instance = DeterministicInitialGameSetup(numPlayers)
        }
        return instance as InitialGameSetup
    }

    companion object {
        private var instance: InitialGameSetup? = null
    }
}