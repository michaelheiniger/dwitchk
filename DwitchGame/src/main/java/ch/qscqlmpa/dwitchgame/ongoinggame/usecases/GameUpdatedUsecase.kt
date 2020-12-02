package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
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
