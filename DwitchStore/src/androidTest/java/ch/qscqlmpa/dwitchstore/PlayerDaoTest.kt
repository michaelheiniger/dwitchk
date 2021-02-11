package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.model.Player
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

internal class PlayerDaoTest : BaseInstrumentedTest() {

    @Before
    override fun setup() {
        super.setup()
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
            .assertConnectionState(PlayerConnectionState.CONNECTED)
            .assertReady(false)
        assertThat(player1FromStore.dwitchId).isNotNull() // Do NOT simplify by using "isNotNull", somehow it produces an error

        val player2FromStore = playerDao.getPlayer(player2LocalId)
        PlayerRobot(player2FromStore)
            .assertGameLocalId(gameLocalId!!)
            .assertName("Legolas")
            .assertPlayerRole(PlayerRole.GUEST)
            .assertConnectionState(PlayerConnectionState.CONNECTED)
            .assertReady(false)
        assertThat(player2FromStore.dwitchId).isNotNull() // Do NOT simplify by using "isNotNull", somehow it produces an error

        assertThat(player1FromStore.dwitchId).isNotEqualTo(playersBefore[0].dwitchId)
        assertThat(player1FromStore.dwitchId).isNotEqualTo(player2FromStore.dwitchId)

        assertThat(player2FromStore.dwitchId).isNotEqualTo(playersBefore[0].dwitchId)
        assertThat(player2FromStore.dwitchId).isNotEqualTo(player1FromStore.dwitchId)

        val playersAfter = playerDao.getAllPlayersSortedOnNameAsc()
        assertThat(playersAfter.size).isEqualTo(3)
    }

    @Test
    fun observePlayersInWaitingRoom() {
        bootstrapDb(
            listOf(
                Player(0, PlayerDwitchId(2), 0, "Saruman", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true),
                Player(0, PlayerDwitchId(3), 0, "Gimli", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, false),
                Player(0, PlayerDwitchId(4), 0, "Boromir", PlayerRole.GUEST, PlayerConnectionState.DISCONNECTED, true),
                Player(0, PlayerDwitchId(5), 0, "Legolas", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true)
            )
        )

        dudeWaitAMinute(1)

        val players = playerDao.observePlayersInWaitingRoom(gameLocalId!!).blockingFirst()

        assertThat(players.size).isEqualTo(5)
        assertThat(players[0].name).isEqualTo(hostName)
        assertThat(players[1].name).isEqualTo("Boromir")
        assertThat(players[2].name).isEqualTo("Gimli")
        assertThat(players[3].name).isEqualTo("Legolas")
        assertThat(players[4].name).isEqualTo("Saruman")
    }

    private fun bootstrapDb(players: List<Player> = emptyList()) {
        insertGameForHost(players)
    }
}
