package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.serialization.Serializable

@Serializable
data class Player(val inGameId: PlayerInGameId,
                  val name: String,
                  val cardsInHand: List<Card>,
                  val rank: Rank,
                  val state: PlayerState,
                  val dwitched: Boolean,
                  val hasPickedACard: Boolean,
                  val cardsForExchange: Set<Card> = emptySet()
) {
    val hasNotPickedACard get() = !hasPickedACard

    val isTheOnePlaying get() = state == PlayerState.Playing

    fun toPlayerInfo(): PlayerInfo {
        return PlayerInfo(inGameId, name)
    }
}