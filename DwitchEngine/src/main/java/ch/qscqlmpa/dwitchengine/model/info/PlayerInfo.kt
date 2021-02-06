package ch.qscqlmpa.dwitchengine.model.info

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank

data class PlayerInfo(
    val id: PlayerDwitchId,
    val name: String,
    val rank: Rank,
    val status: PlayerStatus,
    val dwitched: Boolean,
    val cardsInHand: List<Card>,
    val canPass: Boolean,
    val canPickACard: Boolean,
    val canPlay: Boolean,
    val canStartNewRound: Boolean,
    val minimumPlayingCardValueAllowed: CardName
)
