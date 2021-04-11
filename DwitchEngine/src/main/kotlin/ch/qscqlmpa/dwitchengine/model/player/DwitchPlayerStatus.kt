package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class DwitchPlayerStatus {

    @Serializable
    object Playing : DwitchPlayerStatus()

    @Serializable
    object TurnPassed : DwitchPlayerStatus()

    @Serializable
    object Waiting : DwitchPlayerStatus()

    @Serializable
    object Done : DwitchPlayerStatus()
}
