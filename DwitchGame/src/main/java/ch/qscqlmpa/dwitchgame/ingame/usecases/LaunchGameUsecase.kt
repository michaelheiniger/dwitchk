package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ingame.services.GameInitializerService
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class LaunchGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val gameInitializerService: GameInitializerService,
    private val hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    private val communicator: HostCommunicator
) {
    fun launchGame(): Completable {
        return Completable.fromAction {
            store.updateCurrentRoom(RoomType.GAME_ROOM)
            hostGameLifecycleEventRepository.notify(HostGameLifecycleEvent.MovedToGameRoom)
            sendLaunchGameMessage()
        }
    }

    private fun sendLaunchGameMessage() {
        val gameState = if (store.gameIsNew()) gameInitializerService.initializeGameState() else store.getGameState()
        val message = HostMessageFactory.createLaunchGameMessage(gameState)
        communicator.sendMessage(message)
    }
}
