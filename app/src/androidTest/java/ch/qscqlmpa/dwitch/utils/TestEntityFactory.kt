package ch.qscqlmpa.dwitch.utils

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.model.Player

object TestEntityFactory {

    fun createHostPlayer(
        localId: Long = 10L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(100),
        connected: Boolean = true,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Aragorn",
            PlayerRole.HOST,
            connected,
            ready
        )
    }

    fun createGuestPlayer1(
        localId: Long = 11L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(101),
        connected: Boolean = true,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Boromir",
            PlayerRole.GUEST,
            connected,
            ready
        )
    }

    fun createGuestPlayer2(
        localId: Long = 12L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(102),
        connected: Boolean = true,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Celeborn",
            PlayerRole.GUEST,
            connected,
            ready
        )
    }

    fun createGuestPlayer3(
        localId: Long = 13L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(103),
        connected: Boolean = true,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Denethor",
            PlayerRole.GUEST,
            connected,
            ready
        )
    }
}
