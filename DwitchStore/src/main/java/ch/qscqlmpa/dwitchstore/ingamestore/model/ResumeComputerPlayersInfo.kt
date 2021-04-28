package ch.qscqlmpa.dwitchstore.ingamestore.model

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

data class ResumeComputerPlayersInfo(
    val gameCommonId: GameCommonId,
    val playersId: List<DwitchPlayerId>
)