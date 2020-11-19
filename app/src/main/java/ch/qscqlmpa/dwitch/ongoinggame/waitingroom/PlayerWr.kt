package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

data class PlayerWr(
    val inGameId: PlayerInGameId,
    val name: String,
    val ready: Boolean,
    val connectionState: PlayerConnectionState
) {
    constructor(player: Player) : this(player.inGameId, player.name, player.ready, player.connectionState)
}