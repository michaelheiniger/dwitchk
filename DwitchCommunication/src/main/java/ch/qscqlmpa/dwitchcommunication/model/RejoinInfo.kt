package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.Player

data class RejoinInfo(
    val gameCommonId: GameCommonId,
    val player: Player,
    val connectionId: ConnectionId
) {
    fun playerId(): PlayerDwitchId {
        return player.dwitchId
    }
}