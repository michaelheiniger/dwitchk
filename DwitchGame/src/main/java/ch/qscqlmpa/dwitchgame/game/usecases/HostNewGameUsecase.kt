package ch.qscqlmpa.dwitchgame.game.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycle.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class HostNewGameUsecase @Inject constructor(
    private val hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    private val store: Store
) {
    fun hostGame(gameName: String, playerName: String): Completable {
        return Completable.fromAction {
            val result = store.insertGameForHost(gameName, playerName)
            hostGameLifecycleEventRepository.notify(HostGameLifecycleEvent.GameSetup(GameCreatedInfo(result)))
        }
    }
}
