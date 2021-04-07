package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.PlayingOrderRankComparator

internal object PlayingOrder {

    fun getPlayingOrder(players: List<DwitchPlayer>): List<DwitchPlayerId> {
        return players.sortedWith(PlayingOrderRankComparator()).map { p -> p.id }
    }
}
