package ch.qscqlmpa.dwitchengine.actions.startnewgame

import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.TestEntityFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GameBootstrapTest {

    @Test
    fun `Game phase is RoundIsBeginning`() {
        val initialGameSetup = RandomInitialGameSetup(5)

        val playersInfo = listOf(
            TestEntityFactory.createHostPlayerInfo(),
            TestEntityFactory.createGuestPlayer1Info(),
            TestEntityFactory.createGuestPlayer2Info(),
            TestEntityFactory.createGuestPlayer3Info(),
            TestEntityFactory.createGuestPlayer4Info()
        )

        val gameState = GameBootstrap.createNewGame(playersInfo, initialGameSetup)

        GameStateRobot(gameState).assertGamePhase(DwitchGamePhase.RoundIsBeginning)
    }

    @Test
    fun `Asshole is first to play followed by vice-asshole then neutrals then vice-president and finally president`() {
        val initialGameSetup = RandomInitialGameSetup(5)

        val playersInfo = listOf(
            TestEntityFactory.createHostPlayerInfo(),
            TestEntityFactory.createGuestPlayer1Info(),
            TestEntityFactory.createGuestPlayer2Info(),
            TestEntityFactory.createGuestPlayer3Info(),
            TestEntityFactory.createGuestPlayer4Info()
        )

        val gameState = GameBootstrap.createNewGame(playersInfo, initialGameSetup)

        val asshole = gameState.players.values.find { p -> p.rank == DwitchRank.Asshole }
        assertThat(gameState.playingOrder[0]).isEqualTo(asshole!!.id)

        val viceAsshole = gameState.players.values.find { p -> p.rank == DwitchRank.ViceAsshole }
        assertThat(gameState.playingOrder[1]).isEqualTo(viceAsshole!!.id)

        val neutrals = gameState.players.values.filter { p -> p.rank == DwitchRank.Neutral }
        assertThat(gameState.playingOrder[2]).isEqualTo(neutrals[0].id)

        val vicePresident = gameState.players.values.find { p -> p.rank == DwitchRank.VicePresident }
        assertThat(gameState.playingOrder[3]).isEqualTo(vicePresident!!.id)

        val president = gameState.players.values.find { p -> p.rank == DwitchRank.President }
        assertThat(gameState.playingOrder[4]).isEqualTo(president!!.id)
    }

    @Test
    fun `Asshole is Playing while the others are Waiting`() {
        val initialGameSetup = RandomInitialGameSetup(5)

        val playersInfo = listOf(
            TestEntityFactory.createHostPlayerInfo(),
            TestEntityFactory.createGuestPlayer1Info(),
            TestEntityFactory.createGuestPlayer2Info(),
            TestEntityFactory.createGuestPlayer3Info(),
            TestEntityFactory.createGuestPlayer4Info()
        )

        val gameState = GameBootstrap.createNewGame(playersInfo, initialGameSetup)

        val asshole = gameState.players.values.find { p -> p.rank == DwitchRank.Asshole }
        assertThat(asshole!!.status).isEqualTo(DwitchPlayerStatus.Playing)

        val viceAsshole = gameState.players.values.find { p -> p.rank == DwitchRank.ViceAsshole }
        assertThat(viceAsshole!!.status).isEqualTo(DwitchPlayerStatus.Waiting)

        val neutrals = gameState.players.values.filter { p -> p.rank == DwitchRank.Neutral }
        neutrals.forEach { p -> assertThat(p.status).isEqualTo(DwitchPlayerStatus.Waiting) }

        val vicePresident = gameState.players.values.find { p -> p.rank == DwitchRank.VicePresident }
        assertThat(vicePresident!!.status).isEqualTo(DwitchPlayerStatus.Waiting)

        val president = gameState.players.values.find { p -> p.rank == DwitchRank.President }
        assertThat(president!!.status).isEqualTo(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `Current player is Asshole`() {
        val initialGameSetup = RandomInitialGameSetup(5)

        val playersInfo = listOf(
            TestEntityFactory.createHostPlayerInfo(),
            TestEntityFactory.createGuestPlayer1Info(),
            TestEntityFactory.createGuestPlayer2Info(),
            TestEntityFactory.createGuestPlayer3Info(),
            TestEntityFactory.createGuestPlayer4Info()
        )

        val gameState = GameBootstrap.createNewGame(playersInfo, initialGameSetup)

        val asshole = gameState.players.values.find { p -> p.rank == DwitchRank.Asshole }
        assertThat(asshole!!.id).isEqualTo(gameState.currentPlayerId)
    }

    @Test
    fun `Active players contain all players`() {
        val initialGameSetup = RandomInitialGameSetup(5)

        val host = TestEntityFactory.createHostPlayerInfo()
        val guest1 = TestEntityFactory.createGuestPlayer1Info()
        val guest2 = TestEntityFactory.createGuestPlayer2Info()
        val guest3 = TestEntityFactory.createGuestPlayer3Info()
        val guest4 = TestEntityFactory.createGuestPlayer4Info()

        val playersInfo = listOf(
            TestEntityFactory.createHostPlayerInfo(),
            TestEntityFactory.createGuestPlayer1Info(),
            TestEntityFactory.createGuestPlayer2Info(),
            TestEntityFactory.createGuestPlayer3Info(),
            TestEntityFactory.createGuestPlayer4Info()
        )

        val gameState = GameBootstrap.createNewGame(playersInfo, initialGameSetup)

        assertThat(gameState.activePlayers).contains(host.id)
        assertThat(gameState.activePlayers).contains(guest1.id)
        assertThat(gameState.activePlayers).contains(guest2.id)
        assertThat(gameState.activePlayers).contains(guest3.id)
        assertThat(gameState.activePlayers).contains(guest4.id)
        assertThat(gameState.activePlayers.size).isEqualTo(5)
    }
}
