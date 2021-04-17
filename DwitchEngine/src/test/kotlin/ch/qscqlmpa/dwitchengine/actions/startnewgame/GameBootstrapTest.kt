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

    private val host = TestEntityFactory.createHostPlayerInfo()
    private val guest1 = TestEntityFactory.createGuestPlayer1Info()
    private val guest2 = TestEntityFactory.createGuestPlayer2Info()
    private val guest3 = TestEntityFactory.createGuestPlayer3Info()
    private val guest4 = TestEntityFactory.createGuestPlayer4Info()

    private val hostId = host.id
    private val guest1Id = guest1.id
    private val guest2Id = guest2.id
    private val guest3Id = guest3.id
    private val guest4Id = guest4.id

    @Test
    fun `The game starts with phase RoundIsBeginning`() {
        val initialGameSetup = RandomInitialGameSetup(setOf(hostId, guest1Id, guest2Id, guest3Id, guest4Id))
        val playersInfo = listOf(host, guest1, guest2, guest3, guest4)

        val gameState = GameBootstrap.createNewGame(playersInfo, initialGameSetup)

        GameStateRobot(gameState).assertGamePhase(DwitchGamePhase.RoundIsBeginning)
    }

    @Test
    fun `Asshole is first to play followed by vice-asshole then neutrals then vice-president and finally president`() {
        val initialGameSetup = RandomInitialGameSetup(setOf(hostId, guest1Id, guest2Id, guest3Id, guest4Id))
        val playersInfo = listOf(host, guest1, guest2, guest3, guest4)

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
    fun `Initially, the asshole is Playing while the others are Waiting`() {
        val initialGameSetup = RandomInitialGameSetup(setOf(hostId, guest1Id, guest2Id, guest3Id, guest4Id))
        val playersInfo = listOf(host, guest1, guest2, guest3, guest4)

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
    fun `The initial current player is the asshole`() {
        val initialGameSetup = RandomInitialGameSetup(setOf(hostId, guest1Id, guest2Id, guest3Id, guest4Id))
        val playersInfo = listOf(host, guest1, guest2, guest3, guest4)

        val gameState = GameBootstrap.createNewGame(playersInfo, initialGameSetup)

        val asshole = gameState.players.values.find { p -> p.rank == DwitchRank.Asshole }
        assertThat(asshole!!.id).isEqualTo(gameState.currentPlayerId)
    }

    @Test
    fun `Initially, all players are in the active player list`() {
        val initialGameSetup = RandomInitialGameSetup(setOf(hostId, guest1Id, guest2Id, guest3Id, guest4Id))
        val playersInfo = listOf(host, guest1, guest2, guest3, guest4)

        val gameState = GameBootstrap.createNewGame(playersInfo, initialGameSetup)

        assertThat(gameState.activePlayers).contains(hostId)
        assertThat(gameState.activePlayers).contains(guest1Id)
        assertThat(gameState.activePlayers).contains(guest2Id)
        assertThat(gameState.activePlayers).contains(guest3Id)
        assertThat(gameState.activePlayers).contains(guest4Id)
        assertThat(gameState.activePlayers.size).isEqualTo(5)
    }
}
