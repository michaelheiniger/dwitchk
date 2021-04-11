package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class SpecialRuleBreaker {

    abstract val playerId: DwitchPlayerId

    @Serializable
    data class FinishWithJoker(override val playerId: DwitchPlayerId) : SpecialRuleBreaker()

    @Serializable
    data class PlayedOnFirstJack(override val playerId: DwitchPlayerId) : SpecialRuleBreaker()
}
