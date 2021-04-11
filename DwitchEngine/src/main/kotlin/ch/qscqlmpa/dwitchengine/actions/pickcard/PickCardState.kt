package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState

internal class PickCardState(currentGameState: DwitchGameState) : GameStateBase(currentGameState) {

    override fun checkState() {
        super.checkState()
        checkCurrentPlayerStateIsPlaying()
    }
}
