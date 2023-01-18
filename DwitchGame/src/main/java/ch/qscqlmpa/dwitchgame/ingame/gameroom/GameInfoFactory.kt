package ch.qscqlmpa.dwitchgame.ingame.gameroom

import ch.qscqlmpa.dwitchengine.model.game.DwitchPlayerAction
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchPlayerInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

object GameInfoFactory {

    fun createGameDashboardInfo(
        gameInfo: DwitchGameInfo,
        localPlayerId: DwitchPlayerId,
        playersConnected: Map<DwitchPlayerId, Boolean>
    ): GameDashboardInfo {
        val localPlayerInfo = gameInfo.playerInfos.getValue(localPlayerId)
        val localPlayerIsConnected = playersConnected.getValue(localPlayerId)
        val currentPlayerIsDisconnected = !playersConnected.getValue(gameInfo.currentPlayerId)
        val localPlayerIsCurrentPlayer = gameInfo.currentPlayerId == localPlayerId
        val dashboardEnabled = localPlayerIsConnected && localPlayerIsCurrentPlayer

        return GameDashboardInfo(
            playersInfo = gameInfo.playerInfos.values.map { p -> PlayerInfo(p, localPlayerId) },
            localPlayerDashboard = LocalPlayerDashboard(
                cardsInHand = localPlayerInfo.cardsInHand.map { c -> c.copy(selectable = dashboardEnabled && c.selectable) },
                canPass = dashboardEnabled
            ),
            lastPlayerAction = mapDwitchPlayerActionToPlayerAction(gameInfo.lastPlayerAction, gameInfo.playerInfos),
            lastCardPlayed = gameInfo.lastCardPlayed,
            waitingForPlayerReconnection = currentPlayerIsDisconnected && localPlayerIsConnected && !localPlayerIsCurrentPlayer
        )
    }

    fun createEndOfGameInfo(gameInfo: DwitchGameInfo, localPlayerIsHost: Boolean): EndOfRoundInfo {
        // Special case: "start new round" and "end game" should only be performed by the host for technical reasons.
        return EndOfRoundInfo(
            canStartNewRound = gameInfo.newRoundCanBeStarted && localPlayerIsHost,
            canEndGame = localPlayerIsHost,
            playersInfo = gameInfo.playerInfos.values.map { p -> PlayerEndOfRoundInfo(p.name, p.rank) }
        )
    }
}

data class GameDashboardInfo(

    /**
     * Also defines the order of the players
     */
    val playersInfo: List<PlayerInfo>,

    val localPlayerDashboard: LocalPlayerDashboard,

    /**
     * Last action performed by a player in the current round.
     */
    val lastPlayerAction: PlayerAction?,

    /**
     * Last card(s) played sitting on the table.
     */
    val lastCardPlayed: PlayedCards?,

    /**
     * Indicates that another player is disconnected.
     */
    val waitingForPlayerReconnection: Boolean
)

private fun mapDwitchPlayerActionToPlayerAction(
    action: DwitchPlayerAction?,
    playerInfos: Map<DwitchPlayerId, DwitchPlayerInfo>
): PlayerAction? {
    if (action == null) return null
    val actionPlayerInfo = playerInfos.getValue(action.playerId)
    return when (action) {
        is DwitchPlayerAction.PlayCards -> PlayerAction.PlayCards(
            playerName = actionPlayerInfo.name,
            playedCards = action.playedCards,
            clearsTable = action.clearsTable,
            dwitchedPlayedName = playerInfos[action.dwitchedPlayedId]?.name
        )
        is DwitchPlayerAction.PassTurn -> PlayerAction.PassTurn(actionPlayerInfo.name, action.clearsTable)
    }
}

sealed class PlayerAction {
    abstract val playerName: String

    data class PlayCards(
        override val playerName: String,
        val playedCards: PlayedCards,
        val clearsTable: Boolean,
        val dwitchedPlayedName: String? = null
    ) : PlayerAction()

    data class PassTurn(
        override val playerName: String,
        val clearsTable: Boolean,
    ) : PlayerAction()
}

data class PlayerInfo(
    val name: String,
    val rank: DwitchRank,
    val status: DwitchPlayerStatus,
    val dwitched: Boolean,
    val localPlayer: Boolean
) {
    constructor(player: DwitchPlayerInfo, localPlayerId: DwitchPlayerId) : this(
        name = player.name,
        rank = player.rank,
        status = player.status,
        dwitched = player.dwitched,
        localPlayer = player.id == localPlayerId
    )
}

data class LocalPlayerDashboard(
    val cardsInHand: List<DwitchCardInfo>,
    val canPass: Boolean
)

data class PlayerEndOfRoundInfo(
    val name: String,
    val rank: DwitchRank
)

data class EndOfRoundInfo(
    val playersInfo: List<PlayerEndOfRoundInfo>,
    val canStartNewRound: Boolean,
    val canEndGame: Boolean
)
