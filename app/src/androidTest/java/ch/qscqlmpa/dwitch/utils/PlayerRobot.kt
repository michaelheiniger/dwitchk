package ch.qscqlmpa.dwitch.utils

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.assertj.core.api.Assertions.assertThat

class PlayerRobot(private val playerTest: Player) {

    fun assertId(id: Long): PlayerRobot {
        assertThat(playerTest.id).isEqualTo(id)
        return this
    }

    fun assertDwitchId(dwitchId: PlayerDwitchId): PlayerRobot {
        assertThat(playerTest.dwitchId).isEqualTo(dwitchId)
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