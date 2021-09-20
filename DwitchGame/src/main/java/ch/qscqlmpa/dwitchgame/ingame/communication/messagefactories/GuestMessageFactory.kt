package ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

object GuestMessageFactory {

    fun createJoinGameMessage(playerName: String, computerManaged: Boolean = false): Message {
        return Message.JoinGameMessage(playerName, computerManaged)
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
