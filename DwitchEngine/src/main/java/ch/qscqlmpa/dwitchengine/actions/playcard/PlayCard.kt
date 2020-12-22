package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerState

internal class PlayCard(
    private val playCardState: PlayCardState,
    private val gameUpdater: PlayCardGameUpdater
) {

    private val localPlayerId = playCardState.currentPlayerId()
    private val cardPlayed = playCardState.cardPlayed()

    fun getUpdatedGameState(): GameState {
        playCardState.checkState()

        gameUpdater.setGamePhase(GamePhase.RoundIsOnGoing)
        gameUpdater.undwitchAllPlayers()
        gameUpdater.resetGameEvent()

        gameUpdater.takeCardFromHandAndPutOnTable(localPlayerId, cardPlayed)

        if (playCardState.roundIsOver()) {
            gameUpdater.playerIsDone(localPlayerId, playCardState.cardPlayedIsJoker())
            gameUpdater.playerIsDone(playCardState.getLastActivePlayer(), playCardState.cardPlayedIsJoker())
            gameUpdater.roundIsOver(playCardState.computeRanks(localPlayerId, playCardState.getLastActivePlayer()))
        } else {
            val newCurrentPlayer = playCardState.findNewCurrentPlayer()
            gameUpdater.updateCurrentPlayer(newCurrentPlayer.inGameId)

            setLocalPlayerWaitingIfCannotPlay(newCurrentPlayer)

            if (playCardState.cardPlayedIsJoker() || playCardState.noOtherPlayerCanPlay()) {
                gameUpdater.clearTable()
                gameUpdater.setPlayersWhoPassedTheirTurnedToWaiting()
            }

            if (!playCardState.noOtherPlayerCanPlay()) {
                dwitchNextWaitingPlayerIfAny()
            }

            if (playCardState.currentPlayerHasNoMoreCards()) {
                gameUpdater.playerIsDone(localPlayerId, playCardState.cardPlayedIsJoker())
            }

            // Prevents from picking more than one card while no one else has played in-between
            if (newCurrentPlayer.inGameId != localPlayerId) {
                gameUpdater.resetPlayerHasPickedCard(localPlayerId)
            }
        }

        return gameUpdater.buildUpdatedGameState()
    }

    private fun setLocalPlayerWaitingIfCannotPlay(newCurrentPlayer: Player) {
        if (localPlayerId != newCurrentPlayer.inGameId) {
            gameUpdater.setPlayerState(localPlayerId, PlayerState.Waiting)
        }
    }

    private fun dwitchNextWaitingPlayerIfAny() {
        val nextWaitingPlayer = playCardState.nextWaitingPlayer()
        if (nextWaitingPlayer != null && playCardState.nextWaitingPlayerIsDwitched()) {
            gameUpdater.dwitchPlayer(nextWaitingPlayer.inGameId)
        }
    }
}