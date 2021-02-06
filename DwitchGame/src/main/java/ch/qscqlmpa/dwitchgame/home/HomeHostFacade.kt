package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchmodel.game.ResumableGameInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface HomeHostFacade {

    fun hostGame(gameName: String, playerName: String, gamePort: Int): Completable

    fun resumeGame(gameId: Long, gamePort: Int): Completable

    fun resumableGames(): Observable<List<ResumableGameInfo>>
}
