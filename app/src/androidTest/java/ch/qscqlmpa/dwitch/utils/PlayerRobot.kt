package ch.qscqlmpa.dwitch.utils

import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import org.assertj.core.api.Assertions.assertThat

class PlayerRobot(private val playerTest: Player) {

    fun assertId(id: Long): PlayerRobot {
        assertThat(playerTest.id).isEqualTo(id)
        return this
    }

    fun assertInGameId(inGameId: PlayerInGameId): PlayerRobot {
        assertThat(playerTest.inGameId).isEqualTo(inGameId)
        return this
    }

    fun assertGameLocalId(gameLocalId: Long): PlayerRobot {
        assertThat(playerTest.gameLocalId).isEqualTo(gameLocalId)
        return this
    }

    fun assertName(name: String): PlayerRobot {
        assertThat(playerTest.name).isEqualTo(name)
        return this
    }

    fun assertPlayerRole(playerRole: PlayerRole): PlayerRobot {
        assertThat(playerTest.playerRole).isEqualTo(playerRole)
        return this
    }

    fun assertState(state: PlayerConnectionState): PlayerRobot {
        assertThat(playerTest.connectionState).isEqualTo(state)
        return this
    }

    fun assertReady(ready: Boolean): PlayerRobot {
        assertThat(playerTest.ready).isEqualTo(ready)
        return this
    }
}