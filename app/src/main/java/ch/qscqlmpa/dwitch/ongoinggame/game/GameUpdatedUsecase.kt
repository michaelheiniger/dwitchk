package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.MessageFactory
import ch.qscqlmpa.dwitchengine.model.game.GameInfo
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class GameUpdatedUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: GameCommunicator
) {

    fun handleUpdatedGameState(gameInfo: GameInfo): Completable {
        return Single.fromCallable {
            store.updateGameState(gameInfo.gameState)
            MessageFactory.createGameStateUpdatedMessage(gameInfo.gameState)
        }.flatMapCompletable(communicator::sendGameState)
    }
}
