package ch.qscqlmpa.dwitchengine.initialgamesetup

interface InitialGameSetupFactory {

    fun getInitialGameSetup(numPlayers: Int): InitialGameSetup
}
