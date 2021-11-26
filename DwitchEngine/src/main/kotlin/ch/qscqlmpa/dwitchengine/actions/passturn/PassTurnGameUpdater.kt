package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.DwitchPlayerAction

internal class PassTurnGameUpdater(currentGameState: DwitchGameState) : GameUpdaterBase(currentGameState) {

    private var clearsTable: Boolean = false

    fun clearTable(): PassTurnGameUpdater {
        clearsTable = true
        gameStateMutable.moveCardsFromTableToGraveyard()
        return this
    }

    override fun buildUpdatedGameState(): DwitchGameState {
        gameStateMutable.lastPlayerAction = DwitchPlayerAction.PassTurn(
            playerId = currentPlayerId,
            clearsTable = clearsTable
        )
        return super.buildUpdatedGameState()
    }
}
