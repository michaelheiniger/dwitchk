package ch.qscqlmpa.dwitch

import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

sealed class PlayerIdTestGuest(val name: String, val inGameId: PlayerInGameId) {
    object Host : PlayerIdTestGuest("Aragorn", PlayerInGameId(10))
    object LocalGuest : PlayerIdTestGuest("Boromir", PlayerInGameId(11))
    object Guest2 : PlayerIdTestGuest("Celeborn", PlayerInGameId(12))
    object Guest3 : PlayerIdTestGuest("Denethor", PlayerInGameId(13))
}

