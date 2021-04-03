package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.info.CardItem

data class PlayerDashboard(
    val canStartNewRound: Boolean,
    val canPickACard: Boolean,
    val canPass: Boolean,
    val canPlay: Boolean,
    val cardsInHands: List<CardItem>
)
