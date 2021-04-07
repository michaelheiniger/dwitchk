package ch.qscqlmpa.dwitch.utils

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.model.Player

object TestEntityFactory {

    fun createHostPlayer(
        localId: Long = 10L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(100),
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
        dwitchId: DwitchPlayerId = DwitchPlayerId(101),
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
        dwitchId: DwitchPlayerId = DwitchPlayerId(102),
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
        dwitchId: DwitchPlayerId = DwitchPlayerId(103),
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
