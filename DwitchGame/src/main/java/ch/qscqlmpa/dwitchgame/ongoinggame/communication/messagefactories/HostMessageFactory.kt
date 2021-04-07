package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.model.RejoinInfo
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.model.Player
import javax.inject.Inject

class HostMessageFactory @Inject constructor(private val store: InGameStore) {

    fun createWaitingRoomStateUpdateMessage(): EnvelopeToSend {
        return createWaitingRoomStateUpdateMessage(store.getPlayersInWaitingRoom())
    }

    fun createJoinAckMessage(
        recipientId: ConnectionId,
        playerDwitchId: PlayerDwitchId
    ): EnvelopeToSend {
        val gameCommonId = store.getGameCommonId()
        val message = Message.JoinGameAckMessage(gameCommonId, playerDwitchId)
        return EnvelopeToSend(Recipient.Single(recipientId), message)
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

        fun createGameStateUpdatedMessage(gameState: GameState): EnvelopeToSend {
            return EnvelopeToSend(Recipient.All, Message.GameStateUpdatedMessage(gameState))
        }

        fun toPlayerWr(player: Player): PlayerWr {
            return PlayerWr(player.dwitchId, player.name, player.playerRole, player.connectionState, player.ready)
        }

        private fun createWaitingRoomStateUpdateMessage(playerList: List<Player>): EnvelopeToSend {
            val message = Message.WaitingRoomStateUpdateMessage(playerList.map(::toPlayerWr))
            return EnvelopeToSend(Recipient.All, message)
        }
    }
}
