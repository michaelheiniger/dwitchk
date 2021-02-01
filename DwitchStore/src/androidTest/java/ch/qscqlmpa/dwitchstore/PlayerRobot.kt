package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.assertj.core.api.Assertions.assertThat

class PlayerRobot(val player: Player) {

    fun assertDwitchId(expectedValue: PlayerDwitchId): PlayerRobot {
        assertThat(player.dwitchId).isEqualTo(expectedValue)
        return this
    }

    fun assertGameLocalId(expectedValue: Long): PlayerRobot {
        assertThat(player.gameLocalId).isEqualTo(expectedValue)
        return this
    }

    fun assertName(expectedValue: String): PlayerRobot {
        assertThat(player.name).isEqualTo(expectedValue)
        return this
    }

    fun assertPlayerRole(expectedValue: PlayerRole): PlayerRobot {
        assertThat(player.playerRole).isEqualTo(expectedValue)
        return this
    }

    fun assertConnectionState(expectedValue: PlayerConnectionState): PlayerRobot {
        assertThat(player.connectionState).isEqualTo(expectedValue)
        return this
    }

    fun assertReady(expectedValue: Boolean): PlayerRobot {
        assertThat(player.ready).isEqualTo(expectedValue)
        return this
    }
}