package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.services.GameInitializerService
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
            store.updateGameRoom(RoomType.GAME_ROOM)
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
