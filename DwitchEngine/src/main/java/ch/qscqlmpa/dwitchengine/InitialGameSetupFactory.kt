package ch.qscqlmpa.dwitchengine

interface InitialGameSetupFactory {

    fun getInitialGameSetup(numPlayers: Int): InitialGameSetup
}