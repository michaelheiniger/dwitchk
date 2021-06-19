package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.model.Player
import org.assertj.core.api.Assertions.assertThat

class PlayerRobot(val player: Player) {

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

    fun assertConnected(): PlayerRobot {
        assertThat(player.connected).isTrue
        return this
    }

    fun assertDisconnected(): PlayerRobot {
        assertThat(player.connected).isFalse
        return this
    }

    fun assertReady(expectedValue: Boolean): PlayerRobot {
        assertThat(player.ready).isEqualTo(expectedValue)
        return this
    }
}
