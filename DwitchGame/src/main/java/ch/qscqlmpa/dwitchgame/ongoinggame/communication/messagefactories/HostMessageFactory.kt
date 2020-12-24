package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.model.RejoinInfo
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class HostMessageFactory @Inject constructor(private val store: InGameStore) {

    fun createWaitingRoomStateUpdateMessage(): Single<EnvelopeToSend> {
        return Single.fromCallable { createWaitingRoomStateUpdateMessage(store.getPlayersInWaitingRoom()) }
    }

    fun createJoinAckMessage(
        recipientId: ConnectionId,
        playerInGameId: PlayerInGameId
    ): Single<EnvelopeToSend> {
        return Single.fromCallable {
            val gameCommonId = store.getGame().gameCommonId
            val message = Message.JoinGameAckMessage(gameCommonId, playerInGameId)
            EnvelopeToSend(Recipient.Single(recipientId), message)
        }
    }

    companion object {

        fun createCancelGameMessage(): EnvelopeToSend {
            return EnvelopeToSend(Recipient.All, Message.CancelGameMessage)
        }

        fun createLaunchGameMessage(gameState: GameState): EnvelopeToSend {
            return EnvelopeToSend(Recipient.All, Message.LaunchGameMessage(gameState))
        }

        fun createRejoinAckMessage(rejoinInfo: RejoinInfo): EnvelopeToSend {
            val message = Message.RejoinGameAckMessage(rejoinInfo)
            return EnvelopeToSend(Recipient.Single(rejoinInfo.connectionId), message)
        }

        fun createGameOverMessage(): EnvelopeToSend {
            return EnvelopeToSend(Recipient.All, Message.GameOverMessage)
        }

        fun createCardExchangeMessage(inGameId: PlayerInGameId, cardExchange: CardExchange, recipient: ConnectionId): EnvelopeToSend {
            val message = Message.CardExchangeMessage(inGameId, cardExchange)
            return EnvelopeToSend(Recipient.Single(recipient), message)
        }

        fun createGameStateUpdatedMessage(gameState: GameState): EnvelopeToSend {
            return EnvelopeToSend(Recipient.All, Message.GameStateUpdatedMessage(gameState))
        }

        private fun createWaitingRoomStateUpdateMessage(playerList: List<Player>): EnvelopeToSend {
            val message = Message.WaitingRoomStateUpdateMessage(playerList)
            return EnvelopeToSend(Recipient.All, message)
        }
    }
}