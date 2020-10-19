package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitchengine.InitialGameSetup
import ch.qscqlmpa.dwitchengine.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.TestInitialGameSetup
import javax.inject.Inject

internal class TestInitialGameSetupFactory @Inject constructor() : InitialGameSetupFactory {

    override fun getInitialGameSetup(numPlayers: Int): InitialGameSetup {
        if (instance == null) {
            instance = TestInitialGameSetup(numPlayers)
        }
        return instance as InitialGameSetup
    }

    companion object {
        private var instance: InitialGameSetup? = null
    }
}