package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card

internal data class PlayerMutable(val inGameId: PlayerInGameId,
                         private val name: String,
                         private val cardsInHand: MutableList<Card>,
                         var rank: Rank,
                         var state: PlayerState,
                         var dwitched: Boolean,
                         var hasPickedCard: Boolean

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
            throw IllegalArgumentException("Player $inGameId does not hold card $card")
        }
    }

    fun toPlayer(): Player {
        return Player(
                inGameId,
                name,
                cardsInHand,
                rank,
                state,
                dwitched,
                hasPickedCard
        )
    }


    companion object {

        fun fromPlayer(player: Player): PlayerMutable {
            return PlayerMutable(
                    player.inGameId,
                    player.name,
                    player.cardsInHand.toMutableList(),
                    player.rank,
                    player.state,
                    player.dwitched,
                    player.hasPickedCard
            )
        }
    }
}