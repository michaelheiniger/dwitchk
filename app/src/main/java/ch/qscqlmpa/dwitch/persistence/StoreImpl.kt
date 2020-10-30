package ch.qscqlmpa.dwitch.persistence

import ch.qscqlmpa.dwitch.model.InsertGameResult
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.model.player.Player
import javax.inject.Inject

class StoreImpl @Inject constructor(private val appRoomDatabase: AppRoomDatabase) : Store {

    override fun insertGameForHost(
        gameName: String,
        hostPlayerName: String
    ): InsertGameResult {
        return appRoomDatabase.gameDao().insertGameForHost(gameName, hostPlayerName)
    }

    override fun insertGameForGuest(
        gameName: String,
        gameCommonId: GameCommonId,
        guestPlayerName: String
    ): InsertGameResult {
        return appRoomDatabase.gameDao().insertGameForGuest(
            gameName,
            gameCommonId,
            guestPlayerName
        )
    }

    override fun getLocalPlayer(gameLocalId: Long): Player {
        return appRoomDatabase.playerDao().getLocalPlayer(gameLocalId)
    }
}