package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState

internal class PassTurnGameUpdater(currentGameState: DwitchGameState) : GameUpdaterBase(currentGameState) {

    fun clearTable() {
        gameStateMutable.moveCardsFromTableToGraveyard()
        gameStateMutable.dwitchGameEvent = DwitchGameEvent.TableHasBeenClearedTurnPassed
    }
}
