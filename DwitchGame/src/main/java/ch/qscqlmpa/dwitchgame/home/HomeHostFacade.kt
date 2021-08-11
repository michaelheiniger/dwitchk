package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface HomeHostFacade {

    fun hostGame(gameName: String, playerName: String): Completable

    fun resumeGame(gameId: Long): Completable

    fun resumableGames(): Observable<List<ResumableGameInfo>>
}
