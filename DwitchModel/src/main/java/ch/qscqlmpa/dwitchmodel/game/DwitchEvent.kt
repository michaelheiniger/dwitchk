package ch.qscqlmpa.dwitchmodel.game

import kotlinx.serialization.Serializable
import org.joda.time.DateTime

@Serializable
sealed class DwitchEvent {

    abstract val id: Long
    abstract val creationDate: DateTime

    abstract fun copyWithId(id: Long): DwitchEvent
}