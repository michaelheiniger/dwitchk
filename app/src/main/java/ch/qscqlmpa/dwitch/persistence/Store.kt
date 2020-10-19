package ch.qscqlmpa.dwitch.persistence

import ch.qscqlmpa.dwitch.model.InsertGameResult
import ch.qscqlmpa.dwitch.model.player.Player

interface Store {

    // Game
    fun insertGameForHost(gameName: String, hostPlayerName: String, hostIpAddress: String, hostPort: Int): InsertGameResult
    fun insertGameForGuest(gameName: String, guestPlayerName: String, hostIpAddress: String, hostPort: Int): InsertGameResult

    // Player
    fun getLocalPlayer(gameLocalId: Long): Player
}