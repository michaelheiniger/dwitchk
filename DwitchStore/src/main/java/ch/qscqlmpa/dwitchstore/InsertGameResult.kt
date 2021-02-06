package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

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
