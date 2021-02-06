package ch.qscqlmpa.dwitch.utils

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

object TestEntityFactory {

    fun createHostPlayer(
        localId: Long = 10L,
        dwitchId: PlayerDwitchId = PlayerDwitchId(100),
        connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Aragorn",
            PlayerRole.HOST,
            connectionState,
            ready
        )
    }

    fun createGuestPlayer1(
        localId: Long = 11L,
        dwitchId: PlayerDwitchId = PlayerDwitchId(101),
        connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Boromir",
            PlayerRole.GUEST,
            connectionState,
            ready
        )
    }

    fun createGuestPlayer2(
        localId: Long = 12L,
        dwitchId: PlayerDwitchId = PlayerDwitchId(102),
        connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Celeborn",
            PlayerRole.GUEST,
            connectionState,
            ready
        )
    }

    fun createGuestPlayer3(
        localId: Long = 13L,
        dwitchId: PlayerDwitchId = PlayerDwitchId(103),
        connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Denethor",
            PlayerRole.GUEST,
            connectionState,
            ready
        )
    }
}
