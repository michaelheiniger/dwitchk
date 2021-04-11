package ch.qscqlmpa.dwitchengine.model.game

import kotlinx.serialization.Serializable

@Serializable
sealed class DwitchGamePhase {

    @Serializable
    object RoundIsBeginning : DwitchGamePhase()

    @Serializable
    object CardExchange : DwitchGamePhase()

    @Serializable
    object RoundIsOnGoing : DwitchGamePhase()

    @Serializable
    object RoundIsOver : DwitchGamePhase()

    fun isOneOf(vararg phases: DwitchGamePhase): Boolean {
        return setOf(*phases).contains(this)
    }
}
