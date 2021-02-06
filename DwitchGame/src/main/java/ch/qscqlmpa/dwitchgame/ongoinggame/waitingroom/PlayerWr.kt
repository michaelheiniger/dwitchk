package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

data class PlayerWr(
    val dwitchId: PlayerDwitchId,
    val name: String,
    val ready: Boolean,
    val connectionState: PlayerConnectionState
) {
    constructor(player: Player) : this(player.dwitchId, player.name, player.ready, player.connectionState)
}
