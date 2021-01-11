package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class SpecialRuleBreaker(open val playerId: PlayerDwitchId) {
    data class FinishWithJoker(override val playerId: PlayerDwitchId): SpecialRuleBreaker(playerId)
    data class PlayedOnFirstJack(override val playerId: PlayerDwitchId): SpecialRuleBreaker(playerId)
}