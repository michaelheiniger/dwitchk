package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr

sealed class PlayerGuestTest(val info: PlayerWr) {
    object Host : PlayerGuestTest(
        PlayerWr(
            dwitchId = DwitchPlayerId(10),
            name = "Aragorn",
            playerRole = PlayerRole.HOST,
            connected = true,
            ready = true
        )
    )

    object LocalGuest : PlayerGuestTest(
        PlayerWr(
            dwitchId = DwitchPlayerId(11),
            name = "Boromir",
            playerRole = PlayerRole.GUEST,
            connected = true,
            ready = true
        )
    )

    object Guest2 : PlayerGuestTest(
        PlayerWr(
            dwitchId = DwitchPlayerId(12),
            name = "Celeborn",
            playerRole = PlayerRole.GUEST,
            connected = true,
            ready = true
        )
    )

    object Guest3 : PlayerGuestTest(
        PlayerWr(
            dwitchId = DwitchPlayerId(13),
            name = "Denethor",
            playerRole = PlayerRole.GUEST,
            connected = true,
            ready = true
        )
    )
}
