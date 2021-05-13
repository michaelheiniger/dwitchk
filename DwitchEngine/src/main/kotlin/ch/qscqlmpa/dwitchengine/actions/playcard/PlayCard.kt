package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus

internal class PlayCard(
    private val playCardState: PlayCardState,
    private val gameUpdater: PlayCardGameUpdater
) {

    private val localPlayerId = playCardState.currentPlayerId()
    private val cardPlayed = playCardState.cardsPlayed()

    fun getUpdatedGameState(): DwitchGameState {
        playCardState.checkState()

        gameUpdater.setGamePhase(DwitchGamePhase.RoundIsOnGoing)
        gameUpdater.undwitchAllPlayers()
        gameUpdater.resetGameEvent()

        gameUpdater.takeCardsFromHandAndPutOnTable(localPlayerId, cardPlayed)
        if (playCardState.isLastCardPlayedTheFirstJokerPlayedOfTheRound()) {
            gameUpdater.playerPlayedOnTheFirstJokerPlayedOfTheRound(localPlayerId)
        }

        if (playCardState.roundIsOver()) {
            gameUpdater.playerIsDone(localPlayerId, playCardState.cardPlayedIsJoker())
            gameUpdater.playerIsDone(playCardState.getLastActivePlayer(), playCardState.cardPlayedIsJoker())
            gameUpdater.roundIsOver(playCardState.computeRanks(localPlayerId, playCardState.getLastActivePlayer()))
        } else {
            val newCurrentPlayer = playCardState.findNewCurrentPlayer()
            gameUpdater.updateCurrentPlayer(newCurrentPlayer.id)

            setLocalPlayerWaitingIfCannotPlay(newCurrentPlayer)

            if (playCardState.cardPlayedIsJoker() ||
                newCurrentPlayer.id == playCardState.currentPlayerId() ||
                (playCardState.currentPlayerIsDone() && playCardState.exactlyOneOtherPlayerCanPlay())
            ) {
                gameUpdater.clearTable(cardPlayed)
                gameUpdater.setPlayersWhoPassedTheirTurnedToWaiting()
            }

            dwitchNextWaitingPlayerIfNeeded()

            if (playCardState.currentPlayerHasNoMoreCards()) {
                gameUpdater.playerIsDone(localPlayerId, playCardState.cardPlayedIsJoker())
            }
        }

        return gameUpdater.buildUpdatedGameState()
    }

    private fun setLocalPlayerWaitingIfCannotPlay(newCurrentPlayer: DwitchPlayer) {
        if (localPlayerId != newCurrentPlayer.id) {
            gameUpdater.setPlayerState(localPlayerId, DwitchPlayerStatus.Waiting)
        }
    }

    private fun dwitchNextWaitingPlayerIfNeeded() {
        val nextWaitingPlayer = playCardState.nextWaitingPlayer()
        if (nextWaitingPlayer != null && playCardState.nextWaitingPlayerIsDwitched()) {
            gameUpdater.dwitchPlayer(nextWaitingPlayer.id)
        }
    }
}
