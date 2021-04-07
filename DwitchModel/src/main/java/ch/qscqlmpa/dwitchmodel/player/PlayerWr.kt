package ch.qscqlmpa.dwitchmodel.player

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import kotlinx.serialization.Serializable

@Serializable
data class PlayerWr(
    val dwitchId: DwitchPlayerId,
    val name: String,
    val playerRole: PlayerRole,
    val connectionState: PlayerConnectionState,
    val ready: Boolean
)
