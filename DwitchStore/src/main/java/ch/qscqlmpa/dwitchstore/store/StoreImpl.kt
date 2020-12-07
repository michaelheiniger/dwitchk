package ch.qscqlmpa.dwitchstore.store

import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import ch.qscqlmpa.dwitchstore.db.AppRoomDatabase
import javax.inject.Inject

internal class StoreImpl @Inject constructor(private val appRoomDatabase: AppRoomDatabase) : Store {

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
}