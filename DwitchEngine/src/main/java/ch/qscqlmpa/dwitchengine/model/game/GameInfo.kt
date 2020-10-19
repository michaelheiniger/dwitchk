package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId


data class GameInfo(val gameState: GameState, val localPlayerId: PlayerInGameId)