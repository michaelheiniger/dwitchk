package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

internal class CardExchangePerformerState(
    private val currentGameState: DwitchGameState
) : GameStateBase(currentGameState) {

    private val president = findPlayerWithRank(DwitchRank.President)
    private val vicePresident = findPlayerWithRank(DwitchRank.VicePresident)
    private val asshole = findPlayerWithRank(DwitchRank.Asshole)
    private val viceAsshole = findPlayerWithRank(DwitchRank.ViceAsshole)

    fun numberOfPlayers(): Int {
        return currentGameState.players.size
    }

    fun presidentId(): DwitchPlayerId {
        return president!!.id
    }

    fun vicePresidentId(): DwitchPlayerId {
        return vicePresident!!.id
    }

    fun assholeId(): DwitchPlayerId {
        return asshole!!.id
    }

    fun viceAssholeId(): DwitchPlayerId {
        return viceAsshole!!.id
    }

    fun cardsGivenUpByPresident(): Set<Card> {
        return getCardsForExchangeOfPlayer(president!!.id)
    }

    fun cardsGivenUpByVicePresident(): Set<Card> {
        return getCardsForExchangeOfPlayer(vicePresident!!.id)
    }

    fun cardsGivenUpByAsshole(): Set<Card> {
        return getCardsForExchangeOfPlayer(asshole!!.id)
    }

    fun cardsGivenUpByViceAsshole(): Set<Card> {
        return getCardsForExchangeOfPlayer(viceAsshole!!.id)
    }

    private fun getCardsForExchangeOfPlayer(playerId: DwitchPlayerId): Set<Card> {
        return currentGameState.player(playerId).cardsForExchange
    }

    private fun findPlayerWithRank(rank: DwitchRank): DwitchPlayer? {
        return currentGameState.players.values.find { p -> p.rank == rank }
    }
}
