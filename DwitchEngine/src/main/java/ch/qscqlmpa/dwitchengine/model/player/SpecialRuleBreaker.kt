package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class SpecialRuleBreaker {

    abstract val playerId: PlayerDwitchId

    @Serializable
    data class FinishWithJoker(override val playerId: PlayerDwitchId) : SpecialRuleBreaker()

    @Serializable
    data class PlayedOnFirstJack(override val playerId: PlayerDwitchId) : SpecialRuleBreaker()
}
