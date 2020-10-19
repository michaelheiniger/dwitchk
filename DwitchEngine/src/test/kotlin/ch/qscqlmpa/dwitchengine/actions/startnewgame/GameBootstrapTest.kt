package ch.qscqlmpa.dwitchengine.actions.startnewgame

import ch.qscqlmpa.dwitchengine.RandomInitialGameSetup
import ch.qscqlmpa.dwitchengine.TestEntityFactory
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GameBootstrapTest {

    private val localPlayerId = PlayerInGameId(1L)

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

        val gameState = GameBootstrap.createNewGame(playersInfo, localPlayerId, initialGameSetup)

        val asshole = gameState.players.values.find { p -> p.rank == Rank.Asshole }
        assertThat(gameState.playingOrder[0]).isEqualTo(asshole!!.inGameId)

        val viceAsshole = gameState.players.values.find { p -> p.rank == Rank.ViceAsshole }
        assertThat(gameState.playingOrder[1]).isEqualTo(viceAsshole!!.inGameId)

        val neutrals = gameState.players.values.filter { p -> p.rank == Rank.Neutral }
        assertThat(gameState.playingOrder[2]).isEqualTo(neutrals[0].inGameId)

        val vicePresident = gameState.players.values.find { p -> p.rank == Rank.VicePresident }
        assertThat(gameState.playingOrder[3]).isEqualTo(vicePresident!!.inGameId)

        val president = gameState.players.values.find { p -> p.rank == Rank.President }
        assertThat(gameState.playingOrder[4]).isEqualTo(president!!.inGameId)
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

        val gameState = GameBootstrap.createNewGame(playersInfo, localPlayerId, initialGameSetup)

        val asshole = gameState.players.values.find { p -> p.rank == Rank.Asshole }
        assertThat(asshole!!.state).isEqualTo(PlayerState.Playing)

        val viceAsshole = gameState.players.values.find { p -> p.rank == Rank.ViceAsshole }
        assertThat(viceAsshole!!.state).isEqualTo(PlayerState.Waiting)

        val neutrals = gameState.players.values.filter { p -> p.rank == Rank.Neutral }
        neutrals.forEach { p -> assertThat(p.state).isEqualTo(PlayerState.Waiting) }

        val vicePresident = gameState.players.values.find { p -> p.rank == Rank.VicePresident }
        assertThat(vicePresident!!.state).isEqualTo(PlayerState.Waiting)

        val president = gameState.players.values.find { p -> p.rank == Rank.President }
        assertThat(president!!.state).isEqualTo(PlayerState.Waiting)
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

        val gameState = GameBootstrap.createNewGame(playersInfo, localPlayerId, initialGameSetup)

        val asshole = gameState.players.values.find { p -> p.rank == Rank.Asshole }
        assertThat(asshole!!.inGameId).isEqualTo(gameState.currentPlayerId)
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

        val gameState = GameBootstrap.createNewGame(playersInfo, localPlayerId, initialGameSetup)

        assertThat(gameState.activePlayers).contains(host.id)
        assertThat(gameState.activePlayers).contains(guest1.id)
        assertThat(gameState.activePlayers).contains(guest2.id)
        assertThat(gameState.activePlayers).contains(guest3.id)
        assertThat(gameState.activePlayers).contains(guest4.id)
        assertThat(gameState.activePlayers.size).isEqualTo(5)
    }
}