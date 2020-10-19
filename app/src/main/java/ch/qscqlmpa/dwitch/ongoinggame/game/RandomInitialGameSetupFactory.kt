package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitchengine.InitialGameSetup
import ch.qscqlmpa.dwitchengine.InitialGameSetupFactory
import javax.inject.Inject

class RandomInitialGameSetupFactory @Inject constructor() : InitialGameSetupFactory {

    override fun getInitialGameSetup(numPlayers: Int): InitialGameSetup {
        return RandomInitialGameSetup(numPlayers)
    }
}