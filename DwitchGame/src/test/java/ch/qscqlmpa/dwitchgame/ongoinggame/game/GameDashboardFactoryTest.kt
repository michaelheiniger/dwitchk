package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchgame.BaseUnitTest

internal class GameDashboardFactoryTest : BaseUnitTest() {

//    @Test
//    fun `Set canStartNewRound to false for local player when it is a guest`() {
//        val localPlayerId = PlayerDwitchId(1)
//        val gameInfo = GameInfo(
//            PlayerDwitchId(2),
//            mapOf(
//                PlayerDwitchId(1) to PlayerInfo(
//                    localPlayerId,
//                    "Aragorn",
//                    Rank.Asshole,
//                    PlayerStatus.Done,
//                    false,
//                    emptyList(),
//                    canPass = false,
//                    canPickACard = false,
//                    canPlay = false,
//                    canStartNewRound = true
//                ),
//                PlayerDwitchId(2) to PlayerInfo(
//                    PlayerDwitchId(2),
//                    "Gandalf",
//                    Rank.President,
//                    PlayerStatus.Done,
//                    false,
//                    emptyList(),
//                    canPass = false,
//                    canPickACard = false,
//                    canPlay = false,
//                    canStartNewRound = true
//                )
//            ),
//            GamePhase.RoundIsOver,
//            listOf(PlayerDwitchId(1), PlayerDwitchId(2)),
//            CardName.Two,
//            Card.Blank,
//            listOf(Card.Hearts10),
//            null
//        )
//
//        // Initially to true
//        assertThat(gameInfo.playerInfos.getValue(localPlayerId).canStartNewRound).isTrue
//
//        val localPlayerIsHost = false
//        val dashboard = GameDashboardFactory.createGameDashboardInfo(
//            gameInfo,
//            localPlayerId,
//            localPlayerIsHost,
//            PlayerConnectionState.CONNECTED
//        )
//
//        // Finally set to false because the player is not the host
//        assertThat(dashboard.localPlayerInfo.canStartNewRound).isFalse
//    }
}
