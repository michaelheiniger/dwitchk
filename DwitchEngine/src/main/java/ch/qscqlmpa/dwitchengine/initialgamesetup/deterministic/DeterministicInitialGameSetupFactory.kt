 package ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic

import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory

class DeterministicInitialGameSetupFactory : InitialGameSetupFactory {

    private var instance: InitialGameSetup? = null

    fun setInstance(initialGameSetup: InitialGameSetup) {
        instance = initialGameSetup
    }

    override fun getInitialGameSetup(numPlayers: Int): InitialGameSetup {
        val instanceToReturn = instance
        instance = null
        return instanceToReturn
            ?: throw IllegalStateException("No instance initialized in the factory.")
    }
}