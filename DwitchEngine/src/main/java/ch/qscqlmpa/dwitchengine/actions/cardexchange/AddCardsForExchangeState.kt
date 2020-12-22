package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchengine.rules.CardExchangeComputer

internal class AddCardsForExchangeState(
    currentGameState: GameState,
    private val playerId: PlayerInGameId,
    private val cards: Set<Card>
) : GameStateBase(currentGameState) {

    private val player = currentGameState.player(playerId)
    private val cardExchange = CardExchangeComputer.getCardExchange(playerId, player.rank, player.cardsInHand)

    override fun checkState() {
        super.checkState()
        checkCardExchange()
    }

    fun playerId(): PlayerInGameId {
        return playerId
    }

    fun cardsForExchange(): Set<Card> {
        return cards
    }

    fun cardExchange(): CardExchange {
        return cardExchange!!
    }

    private fun checkCardExchange() {
        val cardExchange = cardExchange
            ?: throw IllegalArgumentException("Player with $playerId is not supposed to exchange any cards !")

        require(cardExchange.numCardsToChoose == cards.size) {
            "Player $playerId must exchange ${cardExchange.numCardsToChoose}, not ${cards.size}"
        }

        //TODO: Check that cards are the cards with the highest value for asshole and viceasshole

        if (player.rank == Rank.Asshole) {
            //TODO: Check values
        } else if (player.rank == Rank.ViceAsshole) {
            //TODO: Check value
        }
    }
}