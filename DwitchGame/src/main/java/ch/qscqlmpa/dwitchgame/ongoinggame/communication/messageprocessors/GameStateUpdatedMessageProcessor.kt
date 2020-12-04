package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

class GameStateUpdatedMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val communicator: GameCommunicator
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        message as Message.GameStateUpdatedMessage

        return Maybe.fromCallable {
            val localPlayer = store.getLocalPlayer()
            val gameStateWithLocalPlayerUpdated = message.gameState
            store.updateGameState(gameStateWithLocalPlayerUpdated)

            return@fromCallable if (localPlayer.playerRole == PlayerRole.HOST) {
                MessageFactory.createGameStateUpdatedMessage(message.gameState)
            } else {
                null
            }
        }.flatMapCompletable { envelopeToSend ->
            if (envelopeToSend != null) communicator.sendMessage(envelopeToSend) else Completable.complete()
        }
    }
}