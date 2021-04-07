package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import kotlinx.serialization.Serializable

@Serializable
data class DwitchCardExchange(
    val playerId: DwitchPlayerId,
    val numCardsToChoose: Int,
    val allowedCardValues: List<CardName>
)
