package ch.qscqlmpa.dwitchmodel.game

import ch.qscqlmpa.dwitchengine.model.card.CardName
import kotlinx.serialization.Serializable
import org.joda.time.DateTime

@Serializable
sealed class DwitchEventBase(
    @Serializable(with = DateTimeSerializer::class) val creationDate: DateTime
) {

    @Serializable
    data class CardExchange(val numCardsToExchange: Int, val allowedCardValues: List<CardName>)
}