package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import kotlinx.serialization.Serializable

@Serializable
data class CardExchange(
    val numCardsToChoose: Int,
    val allowedCardValues: List<CardName>
)
