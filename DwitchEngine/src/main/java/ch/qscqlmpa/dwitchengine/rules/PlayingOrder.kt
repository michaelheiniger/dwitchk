package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayingOrderRankComparator

internal object PlayingOrder {

    fun getPlayingOrder(players: List<Player>): List<PlayerInGameId> {
        return players.sortedWith(PlayingOrderRankComparator()).map { p -> p.id }
    }
}