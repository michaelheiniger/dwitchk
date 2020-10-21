package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitchengine.model.game.GameInfo
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

internal class GameRepository @Inject constructor(private val store: InGameStore) {

    fun getGameInfo(): Single<GameInfo> {
        return Single.fromCallable {
            GameInfo(
                store.getGameState(),
                store.getLocalPlayerInGameId()
            )
        }
    }

    fun observeGameInfo(): Observable<GameInfo> {
        return Observable.combineLatest(
            Observable.fromCallable { store.getLocalPlayerInGameId() },
            store.observeGameState(),
            { localPlayerInGameId, gameState -> GameInfo(gameState, localPlayerInGameId) }
        )
    }
}