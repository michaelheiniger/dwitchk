package ch.qscqlmpa.dwitchengine.initialgamesetup

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

abstract class InitialGameSetup(private val numPlayers: Int) : CardDealer(numPlayers) {

    init {
        require(numPlayers >= 2) { "Minimum number of player is 2, value provided is $numPlayers" }
    }

    abstract fun getRankForPlayer(index: Int): DwitchRank

    companion object {
        const val MAX_NUM_CARDS_PER_PLAYER = 7
    }
}
