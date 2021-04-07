package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

object GuestMessageFactory {

    fun createJoinGameMessage(playerName: String): Message {
        return Message.JoinGameMessage(playerName)
    }

    fun createRejoinGameMessage(gameCommonId: GameCommonId, dwitchPlayerId: DwitchPlayerId): Message {
        return Message.RejoinGameMessage(gameCommonId, dwitchPlayerId)
    }

    fun createLeaveGameMessage(dwitchPlayerId: DwitchPlayerId): Message {
        return Message.LeaveGameMessage(dwitchPlayerId)
    }

    fun createPlayerReadyMessage(dwitchPlayerId: DwitchPlayerId, ready: Boolean): Message {
        return Message.PlayerReadyMessage(dwitchPlayerId, ready)
    }
}
