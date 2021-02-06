package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GameState

internal class PassTurnGameUpdater(currentGameState: GameState) : GameUpdaterBase(currentGameState) {

    fun clearTable() {
        gameStateMutable.moveCardsFromTableToGraveyard()
        gameStateMutable.gameEvent = GameEvent.TableHasBeenClearedTurnPassed
    }
}
