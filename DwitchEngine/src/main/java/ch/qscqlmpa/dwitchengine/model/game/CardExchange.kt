package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.CardName
import kotlinx.serialization.Serializable

@Serializable
data class CardExchange(
    val numCardsToChoose: Int,
    val allowedCardValues: List<CardName>
)
