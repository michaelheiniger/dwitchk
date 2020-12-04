package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.serialization.Serializable

//TODO: Actually use this information
//TODO: rename class ? Use a different way to convey this data than an attribute in GameState ?
@Serializable
sealed class GameEvent {

    @Serializable
    data class TableHasBeenCleared(val lastCardPlayed: Card) : GameEvent()

    @Serializable
    object TableHasBeenClearedTurnPassed : GameEvent()
}