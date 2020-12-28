package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.info.PlayerInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GameInfoForDashboardTest: BaseUnitTest() {

    @Test
    fun `Set canStartNewRound to false for local player when it is a guest`() {
        val localPlayerId = PlayerInGameId(1)
        val gameInfo = GameInfo(
            PlayerInGameId(2),
            mapOf(
                PlayerInGameId(1) to PlayerInfo(
                    localPlayerId,
                    "Aragorn",
                    Rank.Asshole,
                    PlayerStatus.Done,
                    false,
                    emptyList(),
                    canPass = false,
                    canPickACard = false,
                    canPlay = false,
                    canStartNewRound = true,
                    canEndGame = true,
                    CardName.Two
                ),
                PlayerInGameId(2) to PlayerInfo(
                    PlayerInGameId(2),
                    "Gandalf",
                    Rank.President,
                    PlayerStatus.Done,
                    false,
                    emptyList(),
                    canPass = false,
                    canPickACard = false,
                    canPlay = false,
                    canStartNewRound = true,
                    canEndGame = true,
                    CardName.Two
                )
            ),
            GamePhase.RoundIsOver,
            listOf(PlayerInGameId(1), PlayerInGameId(2)),
            CardName.Two,
            Card.Blank,
            listOf(Card.Hearts10),
            null
        )

        // Initially to true
        assertThat(gameInfo.playerInfos.getValue(localPlayerId).canStartNewRound).isTrue

        val localPlayerIsHost = false
        val dashboard = GameInfoForDashboard(gameInfo, localPlayerId, localPlayerIsHost)

        // Finally set to false because the player is not the host
        assertThat(dashboard.localPlayerInfo.canStartNewRound).isFalse
    }
}