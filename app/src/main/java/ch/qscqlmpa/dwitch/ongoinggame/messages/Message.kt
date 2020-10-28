package ch.qscqlmpa.dwitch.ongoinggame.messages

import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import kotlinx.serialization.Serializable

@Serializable
sealed class Message {
    /*****************************************************************************************************************
     * Messages sent by Host
     *****************************************************************************************************************/
    @Serializable
    data class JoinGameAckMessage(val playerInGameId: PlayerInGameId) : Message()

    @Serializable
    data class WaitingRoomStateUpdateMessage(val playerList: List<Player>) : Message()

    @Serializable
    object CancelGameMessage : Message()

    @Serializable
    data class LaunchGameMessage(val gameState: GameState) : Message()

    /*****************************************************************************************************************
     * Messages sent by Guests
     *****************************************************************************************************************/
    @Serializable
    data class JoinGameMessage(val playerName: String) : Message()

    @Serializable
    data class RejoinGameMessage(val playerInGameId: PlayerInGameId) : Message()

    @Serializable
    data class LeaveGameMessage(val playerInGameId: PlayerInGameId) : Message()

    @Serializable
    data class PlayerReadyMessage(val playerInGameId: PlayerInGameId, val ready: Boolean) : Message()

    /*****************************************************************************************************************
     * Messages sent by both Host and Guests
     *****************************************************************************************************************/
    @Serializable
    data class GameStateUpdatedMessage(val gameState: GameState) : Message()

    @Serializable
    object GameOverMessage : Message()
}

