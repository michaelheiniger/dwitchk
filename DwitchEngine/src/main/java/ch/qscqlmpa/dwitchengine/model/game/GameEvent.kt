package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import kotlinx.serialization.Serializable

@Serializable
sealed class GameEvent {

    @Serializable
    data class TableHasBeenCleared(val lastCardPlayed: Card) : GameEvent()

    @Serializable
    object TableHasBeenClearedTurnPassed : GameEvent()
}