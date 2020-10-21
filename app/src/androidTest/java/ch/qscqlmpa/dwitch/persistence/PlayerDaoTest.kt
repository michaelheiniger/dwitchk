package ch.qscqlmpa.dwitch.persistence

import ch.qscqlmpa.dwitch.BaseInstrumentedTest
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.utils.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class PlayerDaoTest : BaseInstrumentedTest() {

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun insertNonLocalPlayer() {
        bootstrapDb()

        val gameLocalIdOfOtherAppInstance = 12312312L
        assertThat(gameLocalIdOfOtherAppInstance).isNotEqualTo(gameLocalId)

        val player1 = Player(43, PlayerInGameId(2), gameLocalIdOfOtherAppInstance, "Saruman", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true)
        val player2 = Player(45, PlayerInGameId(3), gameLocalIdOfOtherAppInstance, "Gimli", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, false)

        val player1LocalId = playerDao.insertNonLocalPlayer(gameLocalId!!, player1)
        val player2LocalId = playerDao.insertNonLocalPlayer(gameLocalId!!, player2)

        val player1FromStore = playerDao.getPlayer(player1LocalId)
        assertThat(player1FromStore.id).isNotEqualTo(player1.id) // Might be true in practice but not in general
        assertThat(player1FromStore).isEqualToIgnoringGivenFields(player1, "id", "gameLocalId")
        assertThat(player1FromStore.gameLocalId).isEqualTo(gameLocalId!!)

        val player2FromStore = playerDao.getPlayer(player2LocalId)
        assertThat(player2FromStore.id).isNotEqualTo(player2.id) // Might be true in practice but not in general
        assertThat(player2FromStore).isEqualToIgnoringGivenFields(player2, "id", "gameLocalId")
        assertThat(player2FromStore.gameLocalId).isEqualTo(gameLocalId!!)
    }

    @Test
    fun insertNewGuestPlayer() {
        bootstrapDb()

        val playersBefore = playerDao.getAllPlayersSortedOnNameAsc()
        assertThat(playersBefore.size).isEqualTo(1)
        assertThat(playersBefore[0].name).isEqualTo(hostName)

        val player1LocalId = playerDao.insertNewGuestPlayer(gameLocalId!!, "Gimli")
        val player2LocalId = playerDao.insertNewGuestPlayer(gameLocalId!!, "Legolas")

        val player1FromStore = playerDao.getPlayer(player1LocalId)
        PlayerRobot(player1FromStore)
                .assertGameLocalId(gameLocalId!!)
                .assertName("Gimli")
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(false)
        assertThat(player1FromStore.inGameId).isNotNull() // Do NOT simplify by using "isNotNull", somehow it produces an error

        val player2FromStore = playerDao.getPlayer(player2LocalId)
        PlayerRobot(player2FromStore)
                .assertGameLocalId(gameLocalId!!)
                .assertName("Legolas")
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(false)
        assertThat(player2FromStore.inGameId).isNotNull() // Do NOT simplify by using "isNotNull", somehow it produces an error

        assertThat(player1FromStore.inGameId).isNotEqualTo(playersBefore[0].inGameId)
        assertThat(player1FromStore.inGameId).isNotEqualTo(player2FromStore.inGameId)

        assertThat(player2FromStore.inGameId).isNotEqualTo(playersBefore[0].inGameId)
        assertThat(player2FromStore.inGameId).isNotEqualTo(player1FromStore.inGameId)

        val playersAfter = playerDao.getAllPlayersSortedOnNameAsc()
        assertThat(playersAfter.size).isEqualTo(3)
    }

    @Test
    fun observeConnectedPlayers() {
        bootstrapDb(listOf(
                Player(0, PlayerInGameId(2), 0, "Saruman", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true),
                Player(0, PlayerInGameId(3), 0, "Gimli", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, false),
                Player(0, PlayerInGameId(4), 0, "Boromir", PlayerRole.GUEST, PlayerConnectionState.DISCONNECTED, true),
                Player(0, PlayerInGameId(5), 0, "Legolas", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true)
        ))

        dudeWaitAMinute(3)

        // Only players that are connected are emitted
        val players = playerDao.observeConnectedPlayers(gameLocalId!!).blockingFirst()

        assertThat(players.size).isEqualTo(4)
        assertThat(players[0].name).isEqualTo(hostName)
        assertThat(players[1].name).isEqualTo("Gimli")
        assertThat(players[2].name).isEqualTo("Legolas")
        assertThat(players[3].name).isEqualTo("Saruman")
    }

    private fun bootstrapDb(players: List<Player> = emptyList()) {
        insertGameForHost(players)
    }
}