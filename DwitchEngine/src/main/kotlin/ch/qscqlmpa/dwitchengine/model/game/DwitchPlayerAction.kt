package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import kotlinx.serialization.Serializable

@Serializable
sealed class DwitchPlayerAction {
    abstract val playerId: DwitchPlayerId

    @Serializable
    data class PlayCards(
        override val playerId: DwitchPlayerId,
        val playedCards: PlayedCards,
        val clearsTable: Boolean,
        val dwitchedPlayedId: DwitchPlayerId? = null
    ) : DwitchPlayerAction()

    @Serializable
    data class PassTurn(
        override val playerId: DwitchPlayerId,
        val clearsTable: Boolean
    ) : DwitchPlayerAction()
}
