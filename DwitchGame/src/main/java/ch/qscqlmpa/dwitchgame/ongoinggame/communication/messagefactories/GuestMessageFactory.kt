package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories


import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

object GuestMessageFactory {

    fun createJoinGameMessage(playerName: String): Message {
        return Message.JoinGameMessage(playerName)
    }

    fun createRejoinGameMessage(gameCommonId: GameCommonId, playerDwitchId: PlayerDwitchId): Message {
        return Message.RejoinGameMessage(gameCommonId, playerDwitchId)
    }

    fun createLeaveGameMessage(playerDwitchId: PlayerDwitchId): Message {
        return Message.LeaveGameMessage(playerDwitchId)
    }

    fun createPlayerReadyMessage(playerDwitchId: PlayerDwitchId, ready: Boolean): Message {
        return Message.PlayerReadyMessage(playerDwitchId, ready)
    }
}