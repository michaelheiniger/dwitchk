package ch.qscqlmpa.dwitch.ongoinggame.messages

import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
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