package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
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

        val player1LocalId = playerDao.insertNewGuestPlayer(gameLocalId!!, "Gimli", computerManaged = false)
        val player2LocalId = playerDao.insertNewGuestPlayer(gameLocalId!!, "Legolas", computerManaged = false)

        val player1FromStore = playerDao.getPlayer(player1LocalId)
        PlayerRobot(player1FromStore)
            .assertGameLocalId(gameLocalId!!)
            .assertName("Gimli")
            .assertPlayerRole(PlayerRole.GUEST)
            .assertConnected()
            .assertReady(false)
        assertThat(player1FromStore.dwitchId).isNotNull // Do NOT simplify by using "isNotNull", somehow it produces an error

        val player2FromStore = playerDao.getPlayer(player2LocalId)
        PlayerRobot(player2FromStore)
            .assertGameLocalId(gameLocalId!!)
            .assertName("Legolas")
            .assertPlayerRole(PlayerRole.GUEST)
            .assertConnected()
            .assertReady(false)
        assertThat(player2FromStore.dwitchId).isNotNull // Do NOT simplify by using "isNotNull", somehow it produces an error

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
                Player(0, DwitchPlayerId(2), 0, "Saruman", PlayerRole.GUEST, connected = true, true),
                Player(0, DwitchPlayerId(3), 0, "Gimli", PlayerRole.GUEST, connected = true, false),
                Player(0, DwitchPlayerId(4), 0, "Boromir", PlayerRole.GUEST, connected = false, true),
                Player(0, DwitchPlayerId(5), 0, "Legolas", PlayerRole.GUEST, connected = true, true)
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

    @Test
    fun getComputerPlayersDwitchId() {
        bootstrapDb(
            listOf(
                Player(0, DwitchPlayerId(2), 0, "Saruman", PlayerRole.GUEST, connected = true, ready = true),
                Player(
                    0,
                    DwitchPlayerId(3),
                    0,
                    "Gimli",
                    PlayerRole.GUEST,
                    connected = true,
                    ready = false,
                    computerManaged = true
                ),
                Player(0, DwitchPlayerId(4), 0, "Boromir", PlayerRole.GUEST, connected = false, ready = true),
                Player(
                    0,
                    DwitchPlayerId(5),
                    0,
                    "Legolas",
                    PlayerRole.GUEST,
                    connected = true,
                    ready = true,
                    computerManaged = true
                )
            )
        )

        val computerPlayersId = playerDao.getComputerPlayersDwitchId(gameLocalId!!)

        assertThat(computerPlayersId).containsExactlyInAnyOrder(DwitchPlayerId(3), DwitchPlayerId(5))
    }

    private fun bootstrapDb(players: List<Player> = emptyList()) {
        insertGameForHost(players)
    }
}
