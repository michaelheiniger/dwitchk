package ch.qscqlmpa.dwitchgame.game.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycle.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class ResumeGameUsecase @Inject constructor(
    private val store: Store,
    private val hostGameLifecycleEventRepository: HostGameLifecycleEventRepository
) {
    fun hostResumedGame(gameId: Long): Completable {
        return Completable.fromAction {
            val game = store.getGame(gameId)
            store.prepareGuestsForGameResume(gameId)
            startHostService(game)
        }
    }

    private fun startHostService(game: Game) {
        hostGameLifecycleEventRepository.notify(
            HostGameLifecycleEvent.GameSetup(
                GameCreatedInfo(
                    game.id,
                    game.localPlayerLocalId
                )
            )
        )
    }
}
