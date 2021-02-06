package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class GameRepository @Inject constructor(private val store: InGameStore) {

    fun getGameState(): GameState {
        return store.getGameState()
    }

    fun getGameInfo(): Single<GameInfo> {
        return Single.fromCallable {
            val localPlayer = store.getLocalPlayer()
            GameInfo(store.getGameState(), localPlayer.dwitchId, localPlayer.playerRole == PlayerRole.HOST)
        }
    }

    fun observeGameInfo(): Observable<GameInfo> {
        return Observable.combineLatest(
            Single.fromCallable { store.getLocalPlayer() }.toObservable(),
            store.observeGameState(),
            { localPlayer, game -> GameInfo(game, localPlayer.dwitchId, localPlayer.playerRole == PlayerRole.HOST) }
        )
    }

    fun updateGameState(gameState: GameState): Completable {
        return Completable.fromAction { store.updateGameState(gameState) }
    }
}
