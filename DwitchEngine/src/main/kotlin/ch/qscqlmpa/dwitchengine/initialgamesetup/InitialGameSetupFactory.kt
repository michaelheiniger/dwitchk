package ch.qscqlmpa.dwitchengine.initialgamesetup

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

interface InitialGameSetupFactory {

    fun getInitialGameSetup(playersId: Set<DwitchPlayerId>): InitialGameSetup
}
