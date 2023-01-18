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

    override fun deleteGamesMarkedForDeletion() {
        appRoomDatabase.gameDao().deleteGamesMarkedForDeletion()
    }

    override fun deleteGame(gameLocalId: Long) {
        appRoomDatabase.gameDao().deleteGameAndPlayers(gameLocalId)
    }

    override fun observeGameCommonIdOfResumableGames(): Observable<List<GameCommonId>> {
        return appRoomDatabase.gameDao().getGameCommonIdOfResumableGames().distinctUntilChanged()
    }

    override fun getResumableGamesInfo(): Observable<List<ResumableGameInfo>> {
        return appRoomDatabase.gameDao().getResumableGamesInfo().distinctUntilChanged()
    }

    override fun getGame(gameId: Long): Game {
        return appRoomDatabase.gameDao().getGame(gameId)
    }

    override fun getGame(gameCommonId: GameCommonId): Game? {
        return appRoomDatabase.gameDao().getGame(gameCommonId)
    }

    /**
     * Intended to be used by the host to reset the local state of the guests when resuming a game.
     */
    override fun prepareGuestsForGameResume(gameId: Long) {
        val localPlayerLocalId = appRoomDatabase.gameDao().getLocalPlayerId(gameId)
        appRoomDatabase.playerDao().prepareGuestsForGameResume(gameId, localPlayerLocalId)
    }

    /**
     * Intended to be used by the guests to reset the local state of the players when joining a resumed game.
     */
    override fun preparePlayersForGameResume(gameId: Long) {
        appRoomDatabase.playerDao().preparePlayersForGameResume(gameId)
    }
}
