package ch.qscqlmpa.dwitchengine.carddealer

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

abstract class CardDealer(private val playersId: Set<DwitchPlayerId>) {

    val numPlayers = playersId.size

    init {
        require(numPlayers in 2..8) // Each player has at least 52/8 == 12 cards
    }

    abstract fun getCardsForPlayer(id: DwitchPlayerId): Set<Card>

    abstract fun getRemainingCards(): Set<Card>

    protected fun checkId(id: DwitchPlayerId) {
        if (!playersId.contains(id)) throw IllegalArgumentException("Argument id $id must be in $playersId")
    }
}
