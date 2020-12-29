package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import kotlinx.serialization.Serializable

@Serializable
data class CardExchange(
    val playerId: PlayerDwitchId,
    val numCardsToChoose: Int,
    val allowedCardValues: List<CardName>
)
