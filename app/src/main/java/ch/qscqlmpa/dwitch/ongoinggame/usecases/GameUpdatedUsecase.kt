package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.MessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitchengine.model.game.GameState
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class GameUpdatedUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: GameCommunicator
) {

    fun handleUpdatedGameState(gameState: GameState): Completable {
        return Single.fromCallable {
            store.updateGameState(gameState)
            MessageFactory.createGameStateUpdatedMessage(gameState)
        }.flatMapCompletable(communicator::sendMessage)
    }
}
