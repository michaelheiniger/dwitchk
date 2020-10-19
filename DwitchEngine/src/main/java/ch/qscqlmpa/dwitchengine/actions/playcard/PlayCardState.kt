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
        checkCardPlayedIsAValidMove(currentGameState, cardPlayed)
    }

    fun cardPlayed(): Card {
        return cardPlayed
    }

    fun roundIsOver(): Boolean {
        return localPlayerHasNoMoreCards() && currentGameState.numberOfActivePlayers() == 2
    }

    fun nextWaitingPlayer(): Player? {
        return currentGameState.nextWaitingPlayer()
    }

    fun localPlayerHasNoMoreCards(): Boolean {
        return currentGameState.localPlayer().cardsInHand.size == 1
    }

    fun localPlayer(): Player {
        return currentGameState.localPlayer()
    }

    fun findNewCurrentPlayer(): Player {
        val nextNonDwitchedWaitingPlayer = nextNonDwitchedWaitingPlayer()
        return if (localPlayerHasNoMoreCards()) {
            nextNonDwitchedWaitingPlayer ?: turnPassedPlayerInOrderAfterLocalPlayer().first()
        } else {
            if (cardPlayedIsJoker() || nextNonDwitchedWaitingPlayer == null) {
                currentGameState.localPlayer()
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

    fun getLastActivePlayer(): PlayerInGameId {
        if (currentGameState.activePlayers.size != 2) {
            throw IllegalStateException("There must be exactly two remaining active players at this step: local player and another one.")
        }

        return currentGameState.activePlayers.find { id -> id != currentGameState.localPlayerId }
                ?: throw IllegalStateException()
    }

    fun computeRanks(penultimatePlayerId: PlayerInGameId, lastPlayerId: PlayerInGameId): Map<PlayerInGameId, Rank> {
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
                .filter { player -> player.state == PlayerState.TurnPassed }
    }

    private fun checkCardPlayedIsAValidMove(gameState: GameState, playedCard: Card) {
        val lastCardOnTable = gameState.lastCardOnTable()
        if (!PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, playedCard, gameState.joker)) {
            throw IllegalArgumentException("The card '$playedCard' may not be played on top of card '$lastCardOnTable'")
        }
    }
}

