package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.model.game.GameInfo
import ch.qscqlmpa.dwitchengine.model.game.GamePhase

internal class PickCard(private val pickCardState: PickCardState, private val gameUpdater: PickCardGameUpdater) {

    fun getUpdatedGameState(): GameInfo {
        pickCardState.checkState()

        gameUpdater.undwitchAllPlayers()
        gameUpdater.resetGameEvent()

        if (pickCardState.gamePhaseIsRoundIsBeginning()) {
            gameUpdater.setGamePhase(GamePhase.RoundIsOnGoing)
        }

        gameUpdater.pickCardFromDeckAndPutItInHands()
        gameUpdater.setPlayerHasPickedCard(pickCardState.localPlayerId())

        return gameUpdater.buildUpdatedGameState()
    }
}