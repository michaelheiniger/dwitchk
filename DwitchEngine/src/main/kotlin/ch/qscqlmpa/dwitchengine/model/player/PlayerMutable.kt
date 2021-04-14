package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card

internal data class PlayerMutable(
    val dwitchId: DwitchPlayerId,
    private val name: String,
    private val cardsInHand: MutableList<Card>,
    var rank: DwitchRank,
    var state: DwitchPlayerStatus,
    var dwitched: Boolean,
    val cardsForExchange: MutableSet<Card>
) {

    fun cardsInHand(): List<Card> {
        return cardsInHand.toList()
    }

    fun hasNoCardsInHand(): Boolean {
        return cardsInHand.isEmpty()
    }

    fun addCardToHand(card: Card) {
        cardsInHand.add(card)
    }

    fun cardsInHand(cards: List<Card>) {
        cardsInHand.clear()
        cardsInHand.addAll(cards)
    }

    fun removeCardFromHand(card: Card) {
        val wasCardInHand = cardsInHand.remove(card)
        if (!wasCardInHand) {
            throw IllegalArgumentException("Player $dwitchId does not hold card $card")
        }
    }

    fun removeCardsFromHand(cards: Set<Card>) {
        cards.forEach(::removeCardFromHand)
    }

    fun removeAllCardsForExchange() {
        cardsForExchange.clear()
    }

    fun toPlayer(): DwitchPlayer {
        return DwitchPlayer(
            dwitchId,
            name,
            cardsInHand,
            rank,
            state,
            dwitched,
            cardsForExchange
        )
    }

    companion object {

        fun fromPlayer(player: DwitchPlayer): PlayerMutable {
            return PlayerMutable(
                player.id,
                player.name,
                player.cardsInHand.toMutableList(),
                player.rank,
                player.status,
                player.dwitched,
                player.cardsForExchange.toMutableSet()
            )
        }
    }
}
