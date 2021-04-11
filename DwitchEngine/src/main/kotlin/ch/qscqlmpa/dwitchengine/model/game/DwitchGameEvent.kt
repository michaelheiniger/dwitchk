package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.serialization.Serializable

// TODO: Actually use this information
// TODO: rename class ? Use a different way to convey this data than an attribute in GameState ?
@Serializable
sealed class DwitchGameEvent {

    @Serializable
    data class TableHasBeenCleared(val lastCardPlayed: Card) : DwitchGameEvent()

    @Serializable
    object TableHasBeenClearedTurnPassed : DwitchGameEvent()
}
