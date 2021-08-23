package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType

data class RejoinInfo(
    val gameCommonId: GameCommonId,
    val currentRoom: RoomType,
    val playerLocalId: Long,
    val dwitchPlayerId: DwitchPlayerId,
    val connectionId: ConnectionId
)
