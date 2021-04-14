package ch.qscqlmpa.dwitchengine.model.info

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

data class DwitchPlayerInfo(
    val id: DwitchPlayerId,
    val name: String,
    val rank: DwitchRank,
    val status: DwitchPlayerStatus,
    val dwitched: Boolean,
    val cardsInHand: List<DwitchCardInfo>,
    val canPlay: Boolean,
    val canStartNewRound: Boolean
)
