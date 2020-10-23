package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.reactivex.Observable
import javax.inject.Inject

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