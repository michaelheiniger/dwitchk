package ch.qscqlmpa.dwitch.model

import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.model.game.GameCommonId

data class InsertGameResult(
    val gameLocalId: Long,
    val gameCommonId: GameCommonId,
    val gameName: String,
    val localPlayerLocalId: Long
) {
    constructor(existingGame: Game) : this(
        existingGame.id,
        existingGame.gameCommonId,
        existingGame.name,
        existingGame.localPlayerLocalId
    )
}

