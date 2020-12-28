package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

class GameInfoForDashboard(
    gameInfo: GameInfo,
    private val localPlayerId: PlayerInGameId,
    localPlayerIsHost: Boolean
) {

    private val gameInfoUpdated: GameInfo

    init {
        // Special case: "start new round" can only be performed by the host for technical reasons.
        val playerInfosMap = gameInfo.playerInfos.toMutableMap()
        val localPlayerInfo = playerInfosMap.getValue(localPlayerId)
        playerInfosMap[localPlayerId] = localPlayerInfo.copy(canStartNewRound = localPlayerInfo.canStartNewRound && localPlayerIsHost)
        gameInfoUpdated = gameInfo.copy(playerInfos = playerInfosMap.toMap())

    }

    val localPlayerInfo get() = gameInfoUpdated.playerInfos.getValue(localPlayerId)

    fun getGameInfo(): GameInfo {
        return gameInfoUpdated
    }
}