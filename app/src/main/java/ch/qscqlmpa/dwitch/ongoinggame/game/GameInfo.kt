package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId


data class GameInfo(val gameState: GameState, val localPlayerId: PlayerInGameId)