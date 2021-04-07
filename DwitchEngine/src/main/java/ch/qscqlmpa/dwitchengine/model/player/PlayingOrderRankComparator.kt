package ch.qscqlmpa.dwitchengine.model.player

/**
 * Determines the playing order from the rank of the players.
 */
internal class PlayingOrderRankComparator : Comparator<DwitchPlayer> {

    override fun compare(player1: DwitchPlayer, player2: DwitchPlayer): Int {
        return playingOrder.getValue(player1.rank).compareTo(playingOrder.getValue(player2.rank))
    }

    companion object {
        private val playingOrder = mapOf(
            DwitchRank.President to 5,
            DwitchRank.VicePresident to 4,
            DwitchRank.Neutral to 3,
            DwitchRank.ViceAsshole to 2,
            DwitchRank.Asshole to 1
        )
    }
}
