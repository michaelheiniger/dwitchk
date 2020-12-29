package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: PlayerDwitchId,
    val name: String,
    val cardsInHand: List<Card>,
    val rank: Rank,
    val status: PlayerStatus,
    val dwitched: Boolean,
    val hasPickedACard: Boolean,
    val cardsForExchange: Set<Card> = emptySet()
) {
    val hasNotPickedACard get() = !hasPickedACard

    val isTheOnePlaying get() = status == PlayerStatus.Playing

    fun toPlayerOnboardingInfo(): PlayerOnboardingInfo {
        return PlayerOnboardingInfo(id, name)
    }
}