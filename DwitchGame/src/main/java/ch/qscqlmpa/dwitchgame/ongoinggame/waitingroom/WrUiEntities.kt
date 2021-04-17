package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

data class PlayerWrUi(val name: String, val connectionState: PlayerConnectionState, val ready: Boolean)
