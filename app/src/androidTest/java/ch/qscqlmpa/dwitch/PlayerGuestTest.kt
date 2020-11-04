package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

sealed class PlayerGuestTest(val name: String, val inGameId: PlayerInGameId) {
    object Host : PlayerGuestTest("Aragorn", PlayerInGameId(10))
    object LocalGuest : PlayerGuestTest("Boromir", PlayerInGameId(11))
    object Guest2 : PlayerGuestTest("Celeborn", PlayerInGameId(12))
    object Guest3 : PlayerGuestTest("Denethor", PlayerInGameId(13))
}

