package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val dwitchId: PlayerDwitchId,
    val name: String,
    val playerRole: PlayerRole,
    val connectionState: PlayerConnectionState,
    val ready: Boolean
) {
    constructor(player: Player) : this(player.dwitchId, player.name, player.playerRole, player.connectionState, player.ready)
}