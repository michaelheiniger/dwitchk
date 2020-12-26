package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class PlayerStatus {

    @Serializable
    object Playing : PlayerStatus()

    @Serializable
    object TurnPassed : PlayerStatus()

    @Serializable
    object Waiting : PlayerStatus()

    @Serializable
    object Done : PlayerStatus()
}