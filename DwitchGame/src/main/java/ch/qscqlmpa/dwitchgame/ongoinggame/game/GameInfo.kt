package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

data class GameInfo(val gameState: GameState, val localPlayerId: PlayerDwitchId, val localPlayerIsHost: Boolean)
