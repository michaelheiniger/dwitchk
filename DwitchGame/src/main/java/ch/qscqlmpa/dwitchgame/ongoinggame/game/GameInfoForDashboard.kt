package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

class GameInfoForDashboard(val gameInfo: GameInfo, private val localPlayerId: PlayerInGameId) {

    val localPlayerInfo get() = gameInfo.playerInfos.getValue(localPlayerId)
}