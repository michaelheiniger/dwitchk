package ch.qscqlmpa.dwitchengine.initialgamesetup

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

abstract class InitialGameSetup(playersId: Set<DwitchPlayerId>) : CardDealer(playersId) {

    abstract fun getRankForPlayer(id: DwitchPlayerId): DwitchRank
}
