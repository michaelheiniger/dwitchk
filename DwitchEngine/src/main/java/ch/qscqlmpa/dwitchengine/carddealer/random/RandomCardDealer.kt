package ch.qscqlmpa.dwitchengine.carddealer.random

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import kotlin.math.floor

class RandomCardDealer(private val numPlayers: Int) : CardDealer(numPlayers) {

    private val cards: List<Card> = CardUtil.deck.shuffled()
    private val numCardsPerPlayer: Int = computeNumCardsPerPlayer()

    override fun getCardsForPlayer(index: Int): List<Card> {
        checkIndex(index)
        val start = 0 + (index * numCardsPerPlayer)
        val end = (index + 1) * numCardsPerPlayer
        return cards.slice(start until end)
    }

    override fun getRemainingCards(): List<Card> {
        val start = numCardsPerPlayer * numPlayers
        val end = cards.size
        return cards.slice(start until end)
    }

    private fun computeNumCardsPerPlayer(): Int {
        return if (cards.size / numPlayers >= InitialGameSetup.MAX_NUM_CARDS_PER_PLAYER) {
            InitialGameSetup.MAX_NUM_CARDS_PER_PLAYER
        } else {
            floor(cards.size.toDouble() / numPlayers).toInt()
        }
    }
}