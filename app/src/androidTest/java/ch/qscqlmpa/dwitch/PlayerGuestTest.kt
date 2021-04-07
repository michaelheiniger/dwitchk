package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

sealed class PlayerGuestTest(val name: String, val id: DwitchPlayerId) {
    object Host : PlayerGuestTest("Aragorn", DwitchPlayerId(10))
    object LocalGuest : PlayerGuestTest("Boromir", DwitchPlayerId(11))
    object Guest2 : PlayerGuestTest("Celeborn", DwitchPlayerId(12))
    object Guest3 : PlayerGuestTest("Denethor", DwitchPlayerId(13))
}
