package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.CardItem
import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.info.PlayerInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

object GameDashboardFactory {

    fun createGameDashboardInfo(
        gameInfo: GameInfo,
        localPlayerId: PlayerDwitchId,
        localPlayerConnectionState: PlayerConnectionState
    ): GameDashboardInfo {
        val localPlayerInfo = gameInfo.playerInfos.getValue(localPlayerId)
        val dashboardEnabled = localPlayerConnectionState == PlayerConnectionState.CONNECTED
        return GameDashboardInfo(
            gameInfo.playerInfos.values.map { p ->
                PlayerInfo2(
                    p.name,
                    p.rank,
                    p.status,
                    p.dwitched,
                    p.id == localPlayerId
                )
            },
            LocalPlayerDashboard(
                dashboardEnabled,
                localPlayerInfo.cardsInHand,
                localPlayerInfo.canPass,
                localPlayerInfo.canPickACard,
                localPlayerInfo.canPlay
            ),
            gameInfo.lastCardPlayed
        )
    }

    fun createEndOfGameInfo(playersInfo: List<PlayerInfo>, localPlayerIsHost: Boolean): EndOfRoundInfo {
        // Special case: "start new round" and "end game" should only be performed by the host for technical reasons.
        return EndOfRoundInfo(
            canStartNewRound = localPlayerIsHost,
            canEndGame = localPlayerIsHost,
            playersInfo = playersInfo.map { p -> PlayerEndOfRoundInfo(p.name, p.rank) }
        )
    }
}

data class GameDashboardInfo(

    /**
     * Also defines the order of the players
     */
    val playersInfo: List<PlayerInfo2>,
    val localPlayerDashboard: LocalPlayerDashboard,

    /**
     * Last card played sitting on the table.
     */
    val lastCardPlayed: Card
)

//TODO: Find proper class name
data class PlayerInfo2(
    val name: String,
    val rank: Rank,
    val status: PlayerStatus,
    val dwitched: Boolean,
    val localPlayer: Boolean
)

data class LocalPlayerDashboard(
    val dashboardEnabled: Boolean,
    val cardsInHand: List<CardItem>,
    val canPass: Boolean,
    val canPickACard: Boolean,
    val canPlay: Boolean
)

data class PlayerEndOfRoundInfo(
    val name: String,
    val rank: Rank
)

data class EndOfRoundInfo(
    val playersInfo: List<PlayerEndOfRoundInfo>,
    val canStartNewRound: Boolean,
    val canEndGame: Boolean
)
