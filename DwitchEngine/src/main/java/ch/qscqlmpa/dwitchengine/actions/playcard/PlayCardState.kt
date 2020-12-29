package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.*
import ch.qscqlmpa.dwitchengine.rules.PlayerMove
import ch.qscqlmpa.dwitchengine.rules.RankComputer

internal class PlayCardState(
    private val currentGameState: GameState,
    private val cardPlayed: Card
) : GameStateBase(currentGameState) {

    override fun checkState() {
        super.checkState()
        checkCurrentPlayerStateIsPlaying()
        checkCardPlayedIsAValidMove()
    }

    fun cardPlayed(): Card {
        return cardPlayed
    }

    fun roundIsOver(): Boolean {
        return currentPlayerHasNoMoreCards() && currentGameState.numberOfActivePlayers() == 2
    }

    fun nextWaitingPlayer(): Player? {
        return currentGameState.nextWaitingPlayer()
    }

    fun currentPlayerHasNoMoreCards(): Boolean {
        return currentGameState.currentPlayer().cardsInHand.size == 1
    }

    fun findNewCurrentPlayer(): Player {
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

    fun noOtherPlayerCanPlay(): Boolean {
        return nextNonDwitchedWaitingPlayer() == null
    }

    fun nextWaitingPlayerIsDwitched(): Boolean {
        val lastCardOnTable = currentGameState.lastCardOnTable()
        return lastCardOnTable != null && lastCardOnTable.value() == cardPlayed.value()
    }

    fun cardPlayedIsJoker() = cardPlayed.name == currentGameState.joker

    fun getLastActivePlayer(): PlayerDwitchId {
        if (currentGameState.activePlayers.size != 2) {
            throw IllegalStateException("There must be exactly two remaining active players at this step: local player and another one.")
        }

        return currentGameState.activePlayers.find { id -> id != currentGameState.currentPlayerId }
                ?: throw IllegalStateException()
    }

    fun computeRanks(penultimatePlayerId: PlayerDwitchId, lastPlayerId: PlayerDwitchId): Map<PlayerDwitchId, Rank> {
        val donePlayersInFinishingOrder = currentGameState.playersDoneForRound.toMutableList()
        donePlayersInFinishingOrder.add(PlayerDone(penultimatePlayerId, cardPlayedIsJoker()))
        donePlayersInFinishingOrder.add(PlayerDone(lastPlayerId, false))
        return RankComputer.computePlayersRank(donePlayersInFinishingOrder.toList())
    }

    private fun nextNonDwitchedWaitingPlayer(): Player? {
        val nextWaitingPlayer = nextWaitingPlayer()
        return if (nextWaitingPlayer != null) {
            waitingPlayerInOrderAfterLocalPlayer()
                    .firstOrNull { player -> if (nextWaitingPlayerIsDwitched()) player != nextWaitingPlayer else true }
        } else {
            null
        }
    }

    private fun waitingPlayerInOrderAfterLocalPlayer(): List<Player> {
        return currentGameState.waitingPlayerInOrderAfterLocalPlayer()
    }

    private fun turnPassedPlayerInOrderAfterLocalPlayer(): List<Player> {
        return currentGameState.activePlayersInPlayingOrderAfterLocalPlayer()
                .filter { player -> player.status == PlayerStatus.TurnPassed }
    }

    private fun checkCardPlayedIsAValidMove() {
        val lastCardOnTable = currentGameState.lastCardOnTable()
        if (!PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, currentGameState.joker)) {
            throw IllegalArgumentException("The card '$cardPlayed' may not be played on top of card '$lastCardOnTable'")
        }
    }
}

