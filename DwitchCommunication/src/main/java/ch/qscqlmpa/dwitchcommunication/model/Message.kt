package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import kotlinx.serialization.Serializable

@Serializable
sealed class Message {
    /*****************************************************************************************************************
     * Messages sent by Host
     *****************************************************************************************************************/
    @Serializable
    data class JoinGameAckMessage(val gameCommonId: GameCommonId, val playerId: DwitchPlayerId) : Message()

    @Serializable
    data class RejoinGameAckMessage(val gameCommonId: GameCommonId, val currentRoom: RoomType, val playerId: DwitchPlayerId) :
        Message() {
        constructor(rejoinInfo: RejoinInfo) : this(rejoinInfo.gameCommonId, rejoinInfo.currentRoom, rejoinInfo.dwitchPlayerId)
    }

    @Serializable
    data class KickPlayerMessage(val playerId: DwitchPlayerId) : Message()

    @Serializable
    data class WaitingRoomStateUpdateMessage(val playerList: List<PlayerWr>) : Message()

    @Serializable
    object CancelGameMessage : Message()

    @Serializable
    data class LaunchGameMessage(val gameState: DwitchGameState) : Message()

    @Serializable
    object GameOverMessage : Message()

    /*****************************************************************************************************************
     * Messages sent by Guests
     *****************************************************************************************************************/
    @Serializable
    data class JoinGameMessage(val playerName: String, val computerManaged: Boolean = false) : Message()

    @Serializable
    data class RejoinGameMessage(val gameCommonId: GameCommonId, val dwitchPlayerId: DwitchPlayerId) : Message()

    @Serializable
    data class LeaveGameMessage(val dwitchPlayerId: DwitchPlayerId) : Message()

    @Serializable
    data class PlayerReadyMessage(val playerId: DwitchPlayerId, val ready: Boolean) : Message()

    /*****************************************************************************************************************
     * Messages sent by both Host and Guests
     *****************************************************************************************************************/
    @Serializable
    data class GameStateUpdatedMessage(val gameState: DwitchGameState) : Message()

    @Serializable
    data class CardsForExchangeMessage(val playerId: DwitchPlayerId, val cards: Set<Card>) : Message()
}
