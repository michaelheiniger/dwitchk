package ch.qscqlmpa.dwitchstore.store

import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.ResumableGameInfo
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.InsertGameResult
import io.reactivex.rxjava3.core.Observable

interface Store {

    fun insertGameForHost(gameName: String, hostPlayerName: String): InsertGameResult

    fun insertGameForGuest(
        gameName: String,
        gameCommonId: GameCommonId,
        guestPlayerName: String
    ): InsertGameResult

    fun updateCurrentRoom(gameId: Long, room: RoomType)

    fun getGameCommonIdOfResumableGames(): Observable<List<GameCommonId>>

    fun getResumableGamesInfo(): Observable<List<ResumableGameInfo>>

    fun getGame(gameId: Long): Game

    fun getGame(gameCommonId: GameCommonId): Game?

    fun prepareGuestsForGameResume(gameId: Long)
}
