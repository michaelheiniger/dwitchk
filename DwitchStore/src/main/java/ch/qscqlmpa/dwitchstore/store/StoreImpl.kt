package ch.qscqlmpa.dwitchstore.store

import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class StoreImpl @Inject constructor(private val appRoomDatabase: AppRoomDatabase) : Store {

    override fun insertGameForHost(gameName: String, hostPlayerName: String): InsertGameResult {
        return appRoomDatabase.gameDao().insertGameForHost(gameName, hostPlayerName)
    }

    override fun insertGameForGuest(gameName: String, gameCommonId: GameCommonId, guestPlayerName: String): InsertGameResult {
        return appRoomDatabase.gameDao().insertGameForGuest(gameName, gameCommonId, guestPlayerName)
    }

    override fun getGameCommonIdOfResumableGames(): Observable<List<GameCommonId>> {
        return appRoomDatabase.gameDao().getGameCommonIdOfResumableGames()
    }

    override fun getResumableGamesInfo(): Observable<List<ResumableGameInfo>> {
        return appRoomDatabase.gameDao().getResumableGamesInfo()
    }

    override fun getGame(gameId: Long): Game {
        return appRoomDatabase.gameDao().getGame(gameId)
    }

    override fun getGame(gameCommonId: GameCommonId): Game? {
        return appRoomDatabase.gameDao().getGame(gameCommonId)
    }

    override fun prepareGuestsForGameResume(gameId: Long) {
        val game = appRoomDatabase.gameDao().getGame(gameId)
        appRoomDatabase.playerDao().prepareGuestsForGameResume(gameId, game.localPlayerLocalId)
    }
}
