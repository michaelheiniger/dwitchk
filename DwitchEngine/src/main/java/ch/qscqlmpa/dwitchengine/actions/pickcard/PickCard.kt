package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState

internal class PickCard(private val pickCardState: PickCardState, private val gameUpdater: PickCardGameUpdater) {

    fun getUpdatedGameState(): DwitchGameState {
        pickCardState.checkState()

        gameUpdater.setGamePhase(DwitchGamePhase.RoundIsOnGoing)
        gameUpdater.undwitchAllPlayers()
        gameUpdater.resetGameEvent()

        gameUpdater.pickCardFromDeckAndPutItInHands()
        gameUpdater.setPlayerHasPickedCard(pickCardState.currentPlayerId())

        return gameUpdater.buildUpdatedGameState()
    }
}
