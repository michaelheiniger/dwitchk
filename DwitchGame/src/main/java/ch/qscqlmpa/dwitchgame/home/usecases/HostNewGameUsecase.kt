package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
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
            hostGameLifecycleEventRepository.notify(HostGameLifecycleEvent.GameCreated(GameCreatedInfo(result)))
        }
    }
}
