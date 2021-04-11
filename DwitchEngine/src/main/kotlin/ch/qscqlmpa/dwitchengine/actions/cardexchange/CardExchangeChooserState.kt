package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchengine.rules.CardExchangeComputer

internal class CardExchangeChooserState(
    currentGameState: DwitchGameState,
    private val playerId: DwitchPlayerId,
    private val cardsChosen: Set<Card>
) : GameStateBase(currentGameState) {

    private val player = currentGameState.player(playerId)
    private val cardExchange = CardExchangeComputer.getCardExchange(playerId, player.rank, player.cardsInHand.toSet())

    override fun checkState() {
        super.checkState()
        checkCardExchange()
    }

    fun playerId(): DwitchPlayerId {
        return playerId
    }

    fun cardsForExchange(): Set<Card> {
        return cardsChosen
    }

    fun cardExchange(): DwitchCardExchange {
        return cardExchange!!
    }

    private fun checkCardExchange() {
        val cardExchange = cardExchange
            ?: throw IllegalArgumentException("Player with $playerId is not supposed to exchange any cards !")

        require(cardExchange.numCardsToChoose == cardsChosen.size) {
            "Player $playerId must exchange ${cardExchange.numCardsToChoose}, not ${cardsChosen.size}"
        }

        if (player.rank == DwitchRank.Asshole || player.rank == DwitchRank.ViceAsshole) {
            checkCardsChosenIsAllowed(cardsChosen, player.cardsInHand)
        }
    }

    private fun checkCardsChosenIsAllowed(cardsChosen: Set<Card>, cardsInHand: List<Card>) {
        val allowCardValues = CardExchangeComputer.getValueOfNCardsWithHighestValue(cardsInHand.toSet(), cardsChosen.size)
            .toMutableList()
        this.cardsChosen.forEach { card ->
            if (!allowCardValues.remove(card.name)) {
                throw IllegalArgumentException("Card chosen for exchange is not allowed: there exists a card with higher value than $card")
            }
        }
    }
}
