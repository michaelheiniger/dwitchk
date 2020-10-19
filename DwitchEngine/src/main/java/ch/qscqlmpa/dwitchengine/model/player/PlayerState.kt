package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class PlayerState {

    @Serializable
    object Playing : PlayerState()

    @Serializable
    object TurnPassed : PlayerState()

    @Serializable
    object Waiting : PlayerState()

    @Serializable
    object Done : PlayerState()
}