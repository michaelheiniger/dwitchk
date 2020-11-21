package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
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
            val gameStateWithLocalPlayerUpdated = message.gameState
            store.updateGameState(gameStateWithLocalPlayerUpdated)

            if (localPlayer.playerRole == PlayerRole.HOST) {
                return@fromCallable MessageFactory.createGameStateUpdatedMessage(message.gameState)
            } else {
                return@fromCallable null
            }
        }.flatMapCompletable { envelopeToSend -> communicator.sendMessage(envelopeToSend) }
    }
}