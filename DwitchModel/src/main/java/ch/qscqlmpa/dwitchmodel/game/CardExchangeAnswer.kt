package ch.qscqlmpa.dwitchmodel.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import kotlinx.serialization.Serializable

//TODO: find a better name
@Serializable
data class CardExchangeAnswer(
    val playerId: PlayerInGameId,
    val cardsGiven: List<Card>
)