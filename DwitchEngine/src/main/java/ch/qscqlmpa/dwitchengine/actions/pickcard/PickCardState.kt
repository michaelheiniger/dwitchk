package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.game.GameState

internal class PickCardState(currentGameState: GameState) : GameStateBase(currentGameState) {

    override fun checkState() {
        super.checkState()
        checkCurrentPlayerStateIsPlaying()
    }
}