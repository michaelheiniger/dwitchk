package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.Rank

internal class CardExchangePerformerState(
    private val currentGameState: GameState
) : GameStateBase(currentGameState) {

    private val president = findPlayerWithRank(Rank.President)
    private val vicePresident = findPlayerWithRank(Rank.VicePresident)
    private val asshole = findPlayerWithRank(Rank.Asshole)
    private val viceAsshole = findPlayerWithRank(Rank.ViceAsshole)

    fun numberOfPlayers(): Int {
        return currentGameState.players.size
    }

    fun presidentId(): PlayerInGameId {
        return president!!.inGameId
    }

    fun vicePresidentId(): PlayerInGameId {
        return vicePresident!!.inGameId
    }

    fun assholeId(): PlayerInGameId {
        return asshole!!.inGameId
    }

    fun viceAssholeId(): PlayerInGameId {
        return viceAsshole!!.inGameId
    }

    fun cardsGivenUpByPresident(): Set<Card> {
        return getCardsForExchangeOfPlayer(president!!.inGameId)
    }

    fun cardsGivenUpByVicePresident(): Set<Card> {
        return getCardsForExchangeOfPlayer(vicePresident!!.inGameId)
    }

    fun cardsGivenUpByAsshole(): Set<Card> {
        return getCardsForExchangeOfPlayer(asshole!!.inGameId)
    }

    fun cardsGivenUpByViceAsshole(): Set<Card> {
        return getCardsForExchangeOfPlayer(viceAsshole!!.inGameId)
    }

    private fun getCardsForExchangeOfPlayer(playerId: PlayerInGameId): Set<Card> {
        return currentGameState.player(playerId).cardsForExchange

    }

    private fun findPlayerWithRank(rank: Rank): Player? {
        return currentGameState.players.values.find { p -> p.rank == rank }
    }
}