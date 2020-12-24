package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import javax.inject.Inject

internal class GameRepository @Inject constructor(private val store: InGameStore) {

    fun getGameEngineWithCurrentGameState(): Single<DwitchEngine> {
        return Single.fromCallable { DwitchEngine(store.getGameState()) }
    }

    fun getLocalPlayerId(): PlayerInGameId {
        return store.getLocalPlayerInGameId()
    }

    fun getGameState(): GameState {
        return store.getGameState()
    }

    fun getGameInfo(): Single<GameInfo> {
        return Single.fromCallable { GameInfo(store.getGameState(), store.getLocalPlayerInGameId()) }
    }

    fun updateGameState(gameState: GameState): Completable {
        return Completable.fromAction { store.updateGameState(gameState) }
    }

    fun observeGameInfo(): Observable<GameInfo> {
        return Observable.combineLatest(
            Observable.fromCallable { store.getLocalPlayerInGameId() },
            store.observeGameState()
                .doOnNext { gameState -> Timber.v("observeGameInfo: $gameState")  },
            { localPlayerInGameId, gameState -> GameInfo(gameState, localPlayerInGameId) }
        )
    }
}