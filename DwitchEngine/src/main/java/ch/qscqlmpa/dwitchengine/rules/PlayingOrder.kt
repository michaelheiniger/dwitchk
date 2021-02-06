package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayingOrderRankComparator

internal object PlayingOrder {

    fun getPlayingOrder(players: List<Player>): List<PlayerDwitchId> {
        return players.sortedWith(PlayingOrderRankComparator()).map { p -> p.id }
    }
}
