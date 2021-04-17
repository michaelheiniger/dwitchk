package ch.qscqlmpa.dwitchengine.carddealer.random

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

class RandomCardDealer(private val playersId: Set<DwitchPlayerId>) : CardDealer(playersId) {

    private val cardsToDeal = selectCardsToDeal()
    private val cardsAssignment: Map<DwitchPlayerId, Set<Card>> = assignCardsRandomly()
    private val _remainingCards: Set<Card> by lazy { CardUtil.getAllCardsExcept(cardsToDeal) }

    override fun getCardsForPlayer(id: DwitchPlayerId): Set<Card> {
        checkId(id)
        return cardsAssignment.getValue(id)
    }

    override fun getRemainingCards(): Set<Card> = _remainingCards

    private fun selectCardsToDeal(): Set<Card> {
        val cardsToDeal = CardUtil.deck.toMutableList()
        cardsToDeal.shuffle()

        when (numPlayers) {
            3 -> cardsToDeal.remove(Card.Clubs3)
            5 -> cardsToDeal.removeAll(listOf(Card.Clubs3, Card.Spades3))
            6 -> cardsToDeal.removeAll(listOf(Card.Clubs3, Card.Spades3, Card.Hearts3, Card.Diamonds3))
            7 -> cardsToDeal.removeAll(listOf(Card.Clubs3, Card.Spades3, Card.Hearts3))
            8 -> cardsToDeal.removeAll(listOf(Card.Clubs3, Card.Spades3, Card.Hearts3, Card.Diamonds3))
        }
        return cardsToDeal.toSet()
    }

    private fun assignCardsRandomly(): Map<DwitchPlayerId, Set<Card>> {
        val numCardsPerPlayer = cardsToDeal.size / numPlayers
        val cardsStacks = cardsToDeal.chunked(numCardsPerPlayer)
        return playersId.mapIndexed { index, playerId -> playerId to cardsStacks[index].toSet() }.toMap()
    }
}
