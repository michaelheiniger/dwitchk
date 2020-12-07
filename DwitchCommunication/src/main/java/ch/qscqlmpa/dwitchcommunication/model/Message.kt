package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchmodel.game.CardExchangeAnswer
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.Player
import kotlinx.serialization.Serializable

@Serializable
sealed class Message {
    /*****************************************************************************************************************
     * Messages sent by Host
     *****************************************************************************************************************/
    @Serializable
    data class JoinGameAckMessage(val gameCommonId: GameCommonId, val playerInGameId: PlayerInGameId) : Message()

    @Serializable
    data class RejoinGameAckMessage(val gameCommonId: GameCommonId, val playerInGameId: PlayerInGameId) : Message() {
        constructor(rejoinInfo: RejoinInfo) : this(rejoinInfo.gameCommonId, rejoinInfo.inGameId())
    }

    @Serializable
    data class WaitingRoomStateUpdateMessage(val playerList: List<Player>) : Message()

    @Serializable
    object CancelGameMessage : Message()

    @Serializable
    data class LaunchGameMessage(val gameState: GameState) : Message()

    @Serializable
    object GameOverMessage : Message()

    @Serializable
    data class CardExchangeMessage(val playerInGameId: PlayerInGameId, val cardExchange: CardExchange): Message()

    /*****************************************************************************************************************
     * Messages sent by Guests
     *****************************************************************************************************************/
    @Serializable
    data class JoinGameMessage(val playerName: String) : Message()

    @Serializable
    data class RejoinGameMessage(val gameCommonId: GameCommonId, val playerInGameId: PlayerInGameId) : Message()

    @Serializable
    data class LeaveGameMessage(val playerInGameId: PlayerInGameId) : Message()

    @Serializable
    data class PlayerReadyMessage(val playerInGameId: PlayerInGameId, val ready: Boolean) : Message()

    @Serializable
    data class CardExchangeAnswerMessage(val cardExchangeAnswer: CardExchangeAnswer): Message()

    /*****************************************************************************************************************
     * Messages sent by both Host and Guests
     *****************************************************************************************************************/
    @Serializable
    data class GameStateUpdatedMessage(val gameState: GameState) : Message()
}

