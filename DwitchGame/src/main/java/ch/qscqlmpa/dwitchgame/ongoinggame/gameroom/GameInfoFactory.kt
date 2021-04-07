package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.CardItem
import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.info.PlayerInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

object GameInfoFactory {

    fun createGameDashboardInfo(
        gameInfo: GameInfo,
        localPlayerId: PlayerDwitchId,
        localPlayerConnectionState: PlayerConnectionState
    ): GameDashboardInfo {
        val localPlayerInfo = gameInfo.playerInfos.getValue(localPlayerId)
        val dashboardEnabled = localPlayerConnectionState == PlayerConnectionState.CONNECTED
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
                canPass = localPlayerIsCurrentPlayer && dashboardEnabled && localPlayerInfo.canPass,
                canPickACard = localPlayerIsCurrentPlayer && dashboardEnabled && localPlayerInfo.canPickACard,
                canPlay = localPlayerIsCurrentPlayer && dashboardEnabled && localPlayerInfo.canPlay
            ),
            lastCardPlayed = gameInfo.lastCardPlayed
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

    private fun adjustCardItemSelectability(
        cardItems: List<CardItem>,
        dashboardEnabled: Boolean,
        localPlayerIsCurrentPlayer: Boolean
    ): List<CardItem> {
        return cardItems.map { c -> c.copy(selectable = dashboardEnabled && localPlayerIsCurrentPlayer && c.selectable) }
    }
}

data class GameDashboardInfo(

    /**
     * Also defines the order of the players
     */
    val playersInfo: List<ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.PlayerInfo>,
    val localPlayerDashboard: LocalPlayerDashboard,

    /**
     * Last card played sitting on the table.
     */
    val lastCardPlayed: Card
)

data class PlayerInfo(
    val name: String,
    val rank: Rank,
    val status: PlayerStatus,
    val dwitched: Boolean,
    val localPlayer: Boolean
)

data class LocalPlayerDashboard(
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
