package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

sealed class PlayerGuestTest(val name: String, val id: PlayerDwitchId) {
    object Host : PlayerGuestTest("Aragorn", PlayerDwitchId(10))
    object LocalGuest : PlayerGuestTest("Boromir", PlayerDwitchId(11))
    object Guest2 : PlayerGuestTest("Celeborn", PlayerDwitchId(12))
    object Guest3 : PlayerGuestTest("Denethor", PlayerDwitchId(13))
}
