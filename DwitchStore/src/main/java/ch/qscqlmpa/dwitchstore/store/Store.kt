package ch.qscqlmpa.dwitchstore.store

import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult

interface Store {

    fun insertGameForHost(gameName: String, hostPlayerName: String): InsertGameResult

    fun insertGameForGuest(
        gameName: String,
        gameCommonId: GameCommonId,
        guestPlayerName: String
    ): InsertGameResult
}