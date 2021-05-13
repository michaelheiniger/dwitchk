package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.*
import ch.qscqlmpa.dwitchengine.rules.PlayerMove
import ch.qscqlmpa.dwitchengine.rules.RankComputer
import ch.qscqlmpa.dwitchengine.rules.SpecialRule

internal class PlayCardState(
    private val currentGameState: DwitchGameState,
    private val cardsPlayed: PlayedCards
) : GameStateBase(currentGameState) {

    override fun checkState() {
        super.checkState()
        checkCurrentPlayerStateIsPlaying()
        checkCardPlayedIsAValidMove()
    }

    fun cardsPlayed(): PlayedCards {
        return cardsPlayed
    }

    fun roundIsOver(): Boolean {
        return currentPlayerHasNoMoreCards() && currentGameState.numberOfActivePlayers() == 2
    }

    fun nextWaitingPlayer(): DwitchPlayer? {
        return currentGameState.nextWaitingPlayer()
    }

    fun currentPlayerHasNoMoreCards(): Boolean {
        return currentGameState.currentPlayer().cardsInHand.size == 1
    }

    fun findNewCurrentPlayer(): DwitchPlayer {
        val nextNonDwitchedWaitingPlayer = nextNonDwitchedWaitingPlayer()
        return if (currentPlayerHasNoMoreCards()) {
            nextNonDwitchedWaitingPlayer ?: turnPassedPlayerInOrderAfterLocalPlayer().first()
        } else {
            if (cardPlayedIsJoker() || nextNonDwitchedWaitingPlayer == null) {
                currentGameState.currentPlayer()
            } else {
                nextNonDwitchedWaitingPlayer
            }
        }
    }

    fun activePlayersInPlayingOrderAfterLocalPlayer(): List<DwitchPlayer> {
        return currentGameState.activePlayersInPlayingOrderAfterCurrentPlayer()
    }

    fun nextWaitingPlayerIsDwitched(): Boolean {
        val lastCardOnTable = currentGameState.lastCardsPlayed()
        return lastCardOnTable != null && lastCardOnTable.value == cardsPlayed.value
    }

    fun cardPlayedIsJoker() = cardsPlayed.name == currentGameState.joker

    fun getLastActivePlayer(): DwitchPlayerId {
        if (currentGameState.activePlayers.size != 2) {
            throw IllegalStateException("There must be exactly two remaining active players at this step: local player and another one.")
        }

        return currentGameState.activePlayers.find { id -> id != currentGameState.currentPlayerId }
            ?: throw IllegalStateException()
    }

    fun computeRanks(penultimatePlayerId: DwitchPlayerId, lastPlayerId: DwitchPlayerId): Map<DwitchPlayerId, DwitchRank> {
        val donePlayersInFinishingOrder = currentGameState.playersDoneForRound.toMutableList()
        donePlayersInFinishingOrder.add(penultimatePlayerId)
        val playersWhoBrokeASpecialRule = playersWhoBrokeASpecialRule(penultimatePlayerId)
        donePlayersInFinishingOrder.add(lastPlayerId)
        return RankComputer.computePlayersRank(donePlayersInFinishingOrder, playersWhoBrokeASpecialRule)
    }

    fun isLastCardPlayedTheFirstJokerPlayedOfTheRound(): Boolean {
        return SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(
            currentGameState.cardsOnTable,
            currentGameState.cardsInGraveyard
        )
    }

    fun currentPlayerIsDone(): Boolean {
        // A player with exactly one card that plays a card is done for the round.
        return currentGameState.currentPlayer().cardsInHand.size == 1
    }

    fun exactlyOneOtherPlayerCanPlay(): Boolean {
        return currentGameState.waitingPlayersInOrder().size == 1
    }

    private fun playersWhoBrokeASpecialRule(penultimatePlayerId: DwitchPlayerId) =
        if (cardPlayedIsJoker()) {
            val tmp = currentGameState.playersWhoBrokeASpecialRule.toMutableList()
            tmp.add(SpecialRuleBreaker.FinishWithJoker(penultimatePlayerId))
            tmp
        } else {
            currentGameState.playersWhoBrokeASpecialRule
        }

    private fun nextNonDwitchedWaitingPlayer(): DwitchPlayer? {
        return if (nextWaitingPlayer() != null) {
            currentGameState.waitingPlayersInOrder()
                .firstOrNull { player -> if (nextWaitingPlayerIsDwitched()) player != nextWaitingPlayer() else true }
        } else {
            null
        }
    }

    private fun turnPassedPlayerInOrderAfterLocalPlayer(): List<DwitchPlayer> {
        return currentGameState.activePlayersInPlayingOrderAfterCurrentPlayer()
            .filter { player -> player.status == DwitchPlayerStatus.TurnPassed }
    }

    private fun checkCardPlayedIsAValidMove() {
        val lastCardOnTable = currentGameState.lastCardsPlayed()
        if (!PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardsPlayed, currentGameState.joker)) {
            throw IllegalArgumentException("The card '$cardsPlayed' may not be played on top of card '$lastCardOnTable'")
        }
    }
}
