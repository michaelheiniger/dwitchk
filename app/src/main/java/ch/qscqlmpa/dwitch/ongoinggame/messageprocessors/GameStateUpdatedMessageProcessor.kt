package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.messages.MessageFactory
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class GameStateUpdatedMessageProcessor @Inject constructor(private val store: InGameStore,
                                                           private val communicator: GameCommunicator
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        message as Message.GameStateUpdatedMessage

        return Maybe.fromCallable {
            val localPlayer = store.getLocalPlayer()
            val gameStateWithLocalPlayerUpdated = message.gameState.copy(localPlayerId = localPlayer.inGameId)
            store.updateGameState(gameStateWithLocalPlayerUpdated)

            if (localPlayer.playerRole == PlayerRole.HOST) {
                return@fromCallable MessageFactory.createGameStateUpdatedMessage(message.gameState)
            } else {
                return@fromCallable null
            }
        }.flatMapCompletable { envelopeToSend -> communicator.sendGameState(envelopeToSend) }
    }
}