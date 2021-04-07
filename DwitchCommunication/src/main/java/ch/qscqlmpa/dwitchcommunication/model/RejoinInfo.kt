package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

data class RejoinInfo(
    val gameCommonId: GameCommonId,
    val playerLocalId: Long,
    val dwitchPlayerId: DwitchPlayerId,
    val connectionId: ConnectionId
)
