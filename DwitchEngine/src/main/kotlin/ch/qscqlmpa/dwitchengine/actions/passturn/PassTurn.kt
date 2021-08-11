package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus

internal class PassTurn(
    private val passTurnState: PassTurnState,
    private val gameUpdater: PassTurnGameUpdater
) {

    private val currentPlayerId = passTurnState.currentPlayerId()

    fun getUpdatedGameState(): DwitchGameState {
        passTurnState.checkState()

        gameUpdater.undwitchAllPlayers()
            .resetGameEvent()

        if (passTurnState.onlyOneOtherPlayerCanPlay()) {
            gameUpdater.clearTable()
                // All active players becomes 'Waiting' except the new current player that is 'Playing'.
                .setPlayersWhoPassedTheirTurnedToWaiting()
                .setPlayerState(currentPlayerId, DwitchPlayerStatus.Waiting)
        } else {
            gameUpdater.setPlayerState(currentPlayerId, DwitchPlayerStatus.TurnPassed)
        }

        gameUpdater.updateCurrentPlayer(passTurnState.nextWaitingPlayer().id)

        return gameUpdater.buildUpdatedGameState()
    }
}
