package ch.qscqlmpa.dwitchmodel.player

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import kotlinx.serialization.Serializable

@Serializable
data class PlayerWr(
    val dwitchId: PlayerDwitchId,
    val name: String,
    val playerRole: PlayerRole,
    val connectionState: PlayerConnectionState,
    val ready: Boolean
)
