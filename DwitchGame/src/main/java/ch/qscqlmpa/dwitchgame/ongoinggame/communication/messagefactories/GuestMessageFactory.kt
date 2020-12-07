package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories


import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

object GuestMessageFactory {

    fun createJoinGameMessage(playerName: String): Message {
        return Message.JoinGameMessage(playerName)
    }

    fun createRejoinGameMessage(gameCommonId: GameCommonId, playerInGameId: PlayerInGameId): Message {
        return Message.RejoinGameMessage(gameCommonId, playerInGameId)
    }

    fun createLeaveGameMessage(playerInGameId: PlayerInGameId): Message {
        return Message.LeaveGameMessage(playerInGameId)
    }

    fun createPlayerReadyMessage(playerInGameId: PlayerInGameId, ready: Boolean): Message {
        return Message.PlayerReadyMessage(playerInGameId, ready)
    }
}