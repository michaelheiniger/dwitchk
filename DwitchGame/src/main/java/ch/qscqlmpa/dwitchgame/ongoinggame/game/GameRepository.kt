package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import javax.inject.Inject

import io.reactivex.rxjava3.core.Observable

internal class GameRepository @Inject constructor(private val store: InGameStore) {

    fun getLocalPlayerId(): PlayerInGameId {
        return store.getLocalPlayerInGameId()
    }

    fun getGameState(): GameState {
        return store.getGameState()
    }

    fun observeGameInfo(): Observable<GameInfo> {
        return Observable.combineLatest(
            Observable.fromCallable { store.getLocalPlayerInGameId() },
            store.observeGameState(),
            { localPlayerInGameId, gameState -> GameInfo(gameState, localPlayerInGameId) }
        )
    }
}