package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RecipientType
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
            EnvelopeToSend(RecipientType.SingleGuest(recipientId), message)
        }
    }

    companion object {

        fun createCancelGameMessage(): EnvelopeToSend {
            return EnvelopeToSend(RecipientType.All, Message.CancelGameMessage)
        }

        fun createLaunchGameMessage(gameState: GameState): EnvelopeToSend {
            return EnvelopeToSend(RecipientType.All, Message.LaunchGameMessage(gameState))
        }

        fun createRejoinAckMessage(rejoinInfo: RejoinInfo): EnvelopeToSend {
            val message = Message.RejoinGameAckMessage(rejoinInfo)
            return EnvelopeToSend(RecipientType.SingleGuest(rejoinInfo.connectionID), message)
        }

        fun createCardExchangeMessage(cardExchange: CardExchange, recipient: ConnectionId): EnvelopeToSend {
            val message = Message.CardExchangeMessage(cardExchange)
            return EnvelopeToSend(RecipientType.SingleGuest(recipient), message)
        }

        private fun createWaitingRoomStateUpdateMessage(playerList: List<Player>): EnvelopeToSend {
            val message = Message.WaitingRoomStateUpdateMessage(playerList)
            return EnvelopeToSend(RecipientType.All, message)
        }
    }
}