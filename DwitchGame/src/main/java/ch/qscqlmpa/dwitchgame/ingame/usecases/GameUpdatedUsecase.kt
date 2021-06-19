package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GameUpdatedUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: GameCommunicator
) {

    fun handleUpdatedGameState(gameState: DwitchGameState): Completable {
        return Completable.fromAction {
            store.updateGameState(gameState)
            val message = MessageFactory.createGameStateUpdatedMessage(gameState)
            communicator.sendMessageToHost(message)
        }
    }
}
