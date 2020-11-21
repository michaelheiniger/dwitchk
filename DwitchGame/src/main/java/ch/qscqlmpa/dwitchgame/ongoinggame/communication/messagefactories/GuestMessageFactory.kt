package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories


import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RecipientType
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import javax.inject.Inject

@OngoingGameScope
class GuestMessageFactory @Inject constructor() {

    companion object {

        fun createJoinGameMessage(playerName: String): EnvelopeToSend {
            val message = Message.JoinGameMessage(playerName)
            return EnvelopeToSend(RecipientType.All, message)
        }

        fun createRejoinGameMessage(gameCommonId: GameCommonId, playerInGameId: PlayerInGameId): EnvelopeToSend {
            val message = Message.RejoinGameMessage(gameCommonId, playerInGameId)
            return EnvelopeToSend(RecipientType.All, message)
        }

        fun createLeaveGameMessage(playerInGameId: PlayerInGameId): EnvelopeToSend {
            val message = Message.LeaveGameMessage(playerInGameId)
            return EnvelopeToSend(RecipientType.All, message)
        }

        fun createPlayerReadyMessage(playerInGameId: PlayerInGameId, ready: Boolean): EnvelopeToSend {
            val message = Message.PlayerReadyMessage(playerInGameId, ready)
            return EnvelopeToSend(RecipientType.All, message)
        }
    }
}