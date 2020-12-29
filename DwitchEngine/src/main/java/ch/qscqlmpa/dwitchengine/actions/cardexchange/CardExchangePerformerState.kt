package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
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

    fun presidentId(): PlayerDwitchId {
        return president!!.id
    }

    fun vicePresidentId(): PlayerDwitchId {
        return vicePresident!!.id
    }

    fun assholeId(): PlayerDwitchId {
        return asshole!!.id
    }

    fun viceAssholeId(): PlayerDwitchId {
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

    private fun getCardsForExchangeOfPlayer(playerId: PlayerDwitchId): Set<Card> {
        return currentGameState.player(playerId).cardsForExchange

    }

    private fun findPlayerWithRank(rank: Rank): Player? {
        return currentGameState.players.values.find { p -> p.rank == rank }
    }
}