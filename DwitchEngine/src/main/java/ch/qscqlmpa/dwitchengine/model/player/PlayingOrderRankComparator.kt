package ch.qscqlmpa.dwitchengine.model.player

/**
 * Determines the playing order from the rank of the players.
 */
internal class PlayingOrderRankComparator : Comparator<Player> {

    override fun compare(player1: Player, player2: Player): Int {
        return playingOrder.getValue(player1.rank).compareTo(playingOrder.getValue(player2.rank))
    }

    companion object {
        private val playingOrder = mapOf(
                Rank.President to 5,
                Rank.VicePresident to 4,
                Rank.Neutral to 3,
                Rank.ViceAsshole to 2,
                Rank.Asshole to 1
        )
    }
}