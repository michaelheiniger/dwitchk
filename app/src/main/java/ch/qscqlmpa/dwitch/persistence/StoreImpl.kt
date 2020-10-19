package ch.qscqlmpa.dwitch.persistence

import ch.qscqlmpa.dwitch.model.InsertGameResult
import ch.qscqlmpa.dwitch.model.player.Player
import javax.inject.Inject

class StoreImpl @Inject constructor(private val appRoomDatabase: AppRoomDatabase) : Store {

    override fun insertGameForHost(gameName: String, hostPlayerName: String, hostIpAddress: String, hostPort: Int): InsertGameResult {
        return appRoomDatabase.gameDao().insertGameForHost(gameName, hostPlayerName, hostIpAddress, hostPort)
    }

    override fun insertGameForGuest(gameName: String, guestPlayerName: String, hostIpAddress: String, hostPort: Int): InsertGameResult {
        return appRoomDatabase.gameDao().insertGameForGuest(gameName, guestPlayerName, hostIpAddress, hostPort)
    }

    override fun getLocalPlayer(gameLocalId: Long): Player {
        return appRoomDatabase.playerDao().getLocalPlayer(gameLocalId)
    }
}