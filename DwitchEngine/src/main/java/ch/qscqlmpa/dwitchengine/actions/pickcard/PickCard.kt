package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState

internal class PickCard(private val pickCardState: PickCardState, private val gameUpdater: PickCardGameUpdater) {

    fun getUpdatedGameState(): GameState {
        pickCardState.checkState()

        gameUpdater.setGamePhase(GamePhase.RoundIsOnGoing)
        gameUpdater.undwitchAllPlayers()
        gameUpdater.resetGameEvent()

        gameUpdater.pickCardFromDeckAndPutItInHands()
        gameUpdater.setPlayerHasPickedCard(pickCardState.currentPlayerId())

        return gameUpdater.buildUpdatedGameState()
    }
}
