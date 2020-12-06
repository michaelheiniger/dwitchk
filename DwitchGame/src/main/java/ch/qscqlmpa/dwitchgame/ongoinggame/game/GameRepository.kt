package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class GameRepository @Inject constructor(private val store: InGameStore) {

    fun getLocalPlayerId(): PlayerInGameId {
        return store.getLocalPlayerInGameId()
    }

    fun getGameState(): GameState {
        return store.getGameState()
    }

    fun getGameInfo(): Single<GameInfo> {
        return Single.fromCallable { GameInfo(store.getGameState(), store.getLocalPlayerInGameId()) }

    }

    fun observeGameInfo(): Observable<GameInfo> {
        return Observable.combineLatest(
            Observable.fromCallable { store.getLocalPlayerInGameId() },
            store.observeGameState(),
            { localPlayerInGameId, gameState -> GameInfo(gameState, localPlayerInGameId) }
        )
    }
}