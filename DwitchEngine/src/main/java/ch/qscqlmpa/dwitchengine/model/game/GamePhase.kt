package ch.qscqlmpa.dwitchengine.model.game

import kotlinx.serialization.Serializable

@Serializable
sealed class GamePhase {

    @Serializable
    object RoundIsBeginning : GamePhase()

    @Serializable
    object RoundIsBeginningWithCardExchange : GamePhase()

    @Serializable
    object RoundIsOnGoing : GamePhase()

    @Serializable
    object RoundIsOver : GamePhase()

    fun isOneOf(vararg phases: GamePhase): Boolean {
        return setOf(*phases).contains(this)
    }
}