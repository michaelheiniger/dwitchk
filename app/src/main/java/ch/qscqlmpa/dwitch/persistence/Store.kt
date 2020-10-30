package ch.qscqlmpa.dwitch.persistence

import ch.qscqlmpa.dwitch.model.InsertGameResult
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.model.player.Player

interface Store {

    fun insertGameForHost(gameName: String, hostPlayerName: String): InsertGameResult

    fun insertGameForGuest(
        gameName: String,
        gameCommonId: GameCommonId,
        guestPlayerName: String
    ): InsertGameResult

    fun getLocalPlayer(gameLocalId: Long): Player
}