package ch.qscqlmpa.dwitchmodel.game

import ch.qscqlmpa.dwitchengine.model.card.CardName
import kotlinx.serialization.Serializable
import org.joda.time.DateTime

@Serializable
sealed class DwitchEventBase {

    abstract val id: Long
    abstract val creationDate: DateTime

    abstract fun copyWithId(id: Long): DwitchEventBase

    @Serializable
    data class CardExchange(
        override val id: Long = 0,
        val numCardsToExchange: Int,
        val allowedCardValues: List<CardName>,
        @Serializable(with = DateTimeSerializer::class) override val creationDate: DateTime
    ) : DwitchEventBase() {
        override fun copyWithId(id: Long): DwitchEventBase {
            return this.copy(id = id)
        }
    }
}