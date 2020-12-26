package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus

internal class PassTurn(private val passTurnState: PassTurnState, private val gameUpdater: PassTurnGameUpdater) {

    private val localPlayerId = passTurnState.currentPlayerId()

    fun getUpdatedGameState(): GameState {
        passTurnState.checkState()

        gameUpdater.undwitchAllPlayers()
        gameUpdater.resetGameEvent()

        gameUpdater.updateCurrentPlayer(passTurnState.nextWaitingPlayer().id)

        // The localplayer could not pass its turn if there is only one other waiting player that is dwitched
        // --> the table would be cleared and so localplayer would be Playing (and not CardPicked which is necessary to pass the turn)
        if (passTurnState.onlyOneOtherPlayerCanPlay()) {
            gameUpdater.clearTable()
            gameUpdater.setPlayersWhoPassedTheirTurnedToWaiting()
            gameUpdater.setPlayerState(localPlayerId, PlayerStatus.Waiting)
        } else {
            gameUpdater.setPlayerState(localPlayerId, PlayerStatus.TurnPassed)
        }

        gameUpdater.resetPlayerHasPickedCard(localPlayerId)

        return gameUpdater.buildUpdatedGameState()
    }
}