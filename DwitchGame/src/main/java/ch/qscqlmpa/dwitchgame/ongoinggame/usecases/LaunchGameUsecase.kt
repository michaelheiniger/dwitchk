package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.services.ChangeCurrentRoomService
import ch.qscqlmpa.dwitchgame.ongoinggame.services.GameInitializerService
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject


internal class LaunchGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val gameInitializerService: GameInitializerService,
    private val changeCurrentRoomService: ChangeCurrentRoomService,
    private val communicator: HostCommunicator
) {

    fun launchGame(): Completable {
        return Completable.fromAction {
            sendLaunchGameMessage()
            changeCurrentRoomService.moveToGameRoom()
        }
    }

    private fun sendLaunchGameMessage() {
        val gameState = if (store.gameIsNew()) gameInitializerService.initializeGameState() else store.getGameState()
        val message = HostMessageFactory.createLaunchGameMessage(gameState)
        communicator.sendMessage(message)
    }
}