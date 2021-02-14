package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import kotlinx.serialization.Serializable

@Serializable
sealed class Message {
    /*****************************************************************************************************************
     * Messages sent by Host
     *****************************************************************************************************************/
    @Serializable
    data class JoinGameAckMessage(val gameCommonId: GameCommonId, val playerId: PlayerDwitchId) : Message()

    @Serializable
    data class RejoinGameAckMessage(val gameCommonId: GameCommonId, val playerDwitchId: PlayerDwitchId) : Message() {
        constructor(rejoinInfo: RejoinInfo) : this(rejoinInfo.gameCommonId, rejoinInfo.playerDwitchId)
    }

    @Serializable
    data class WaitingRoomStateUpdateMessage(val playerList: List<PlayerWr>) : Message()

    @Serializable
    object CancelGameMessage : Message()

    @Serializable
    data class LaunchGameMessage(val gameState: GameState) : Message()

    @Serializable
    object GameOverMessage : Message()

    /*****************************************************************************************************************
     * Messages sent by Guests
     *****************************************************************************************************************/
    @Serializable
    data class JoinGameMessage(val playerName: String) : Message()

    @Serializable
    data class RejoinGameMessage(val gameCommonId: GameCommonId, val playerDwitchId: PlayerDwitchId) : Message()

    @Serializable
    data class LeaveGameMessage(val playerDwitchId: PlayerDwitchId) : Message()

    @Serializable
    data class PlayerReadyMessage(val playerId: PlayerDwitchId, val ready: Boolean) : Message()

    /*****************************************************************************************************************
     * Messages sent by both Host and Guests
     *****************************************************************************************************************/
    @Serializable
    data class GameStateUpdatedMessage(val gameState: GameState) : Message()

    @Serializable
    data class CardsForExchangeMessage(val playerId: PlayerDwitchId, val cards: Set<Card>) : Message()
}
