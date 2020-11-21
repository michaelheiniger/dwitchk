package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

data class PlayerWr(
    val inGameId: PlayerInGameId,
    val name: String,
    val ready: Boolean,
    val connectionState: PlayerConnectionState
) {
    constructor(player: Player) : this(player.inGameId, player.name, player.ready, player.connectionState)
}