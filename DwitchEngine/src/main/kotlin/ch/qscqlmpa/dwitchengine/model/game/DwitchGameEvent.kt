package ch.qscqlmpa.dwitchengine.model.game

import kotlinx.serialization.Serializable

// TODO: Actually use this information
// TODO: rename class ? Use a different way to convey this data than an attribute in GameState ?
@Serializable
sealed class DwitchGameEvent {

    @Serializable
    data class TableHasBeenCleared(val lastCardPlayed: PlayedCards) : DwitchGameEvent()

    @Serializable
    object TableHasBeenClearedTurnPassed : DwitchGameEvent()
}
