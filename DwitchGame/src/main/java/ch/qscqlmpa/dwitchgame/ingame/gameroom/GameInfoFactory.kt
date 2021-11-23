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
        val localPlayerIsConnected = playerIsConnected(playersConnected, localPlayerId)
        val dashboardEnabled = localPlayerIsConnected
        val localPlayerIsCurrentPlayer = gameInfo.currentPlayerId == localPlayerId
        return GameDashboardInfo(
            gameInfo.playerInfos.values.map { p ->
                PlayerInfo(
                    p.name,
                    p.rank,
                    p.status,
                    p.dwitched,
                    localPlayer = p.id == localPlayerId
                )
            },
            LocalPlayerDashboard(
                adjustCardItemSelectability(localPlayerInfo.cardsInHand, dashboardEnabled, localPlayerIsCurrentPlayer),
                canPass = localPlayerIsCurrentPlayer && dashboardEnabled
            ),
            lastPlayerAction = mapDwitchPlayerActionToPlayerAction(gameInfo.lastPlayerAction, gameInfo.playerInfos),
            lastCardPlayed = gameInfo.lastCardPlayed,
            waitingForPlayerReconnection = playerIsDisconnected(playersConnected, gameInfo.currentPlayerId) &&
                gameInfo.currentPlayerId != localPlayerId &&
                localPlayerIsConnected
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

    private fun playerIsConnected(
        playersConnectionState: Map<DwitchPlayerId, Boolean>,
        playerId: DwitchPlayerId
    ) = playersConnectionState.getValue(playerId)

    private fun playerIsDisconnected(
        playersConnectionState: Map<DwitchPlayerId, Boolean>,
        playerId: DwitchPlayerId
    ) = !playerIsConnected(playersConnectionState, playerId)

    private fun adjustCardItemSelectability(
        cardItems: List<DwitchCardInfo>,
        dashboardEnabled: Boolean,
        localPlayerIsCurrentPlayer: Boolean
    ) = cardItems.map { c -> c.copy(selectable = dashboardEnabled && localPlayerIsCurrentPlayer && c.selectable) }
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
    dwitchPlayerAction: DwitchPlayerAction?,
    playerInfos: Map<DwitchPlayerId, DwitchPlayerInfo>
): PlayerAction? {
    if (dwitchPlayerAction == null) return null
    val actionPlayerInfo = playerInfos.getValue(dwitchPlayerAction.playerId)
    return when (dwitchPlayerAction) {
        is DwitchPlayerAction.PlayCards -> PlayerAction.PlayCards(
            playerName = actionPlayerInfo.name,
            playedCards = dwitchPlayerAction.playedCards,
            dwitchedPlayedName = playerInfos[dwitchPlayerAction.dwitchedPlayedId]?.name
        )
        is DwitchPlayerAction.PassTurn -> PlayerAction.PassTurn(actionPlayerInfo.name)
    }
}

sealed class PlayerAction {
    abstract val playerName: String

    data class PlayCards(
        override val playerName: String,
        val playedCards: PlayedCards,
        val dwitchedPlayedName: String? = null
    ) : PlayerAction()

    data class PassTurn(
        override val playerName: String,
    ) : PlayerAction()
}

data class PlayerInfo(
    val name: String,
    val rank: DwitchRank,
    val status: DwitchPlayerStatus,
    val dwitched: Boolean,
    val localPlayer: Boolean
)

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
