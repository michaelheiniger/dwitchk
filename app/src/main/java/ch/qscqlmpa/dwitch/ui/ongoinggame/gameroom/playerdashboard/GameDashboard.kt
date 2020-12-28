package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.ImageInfo

data class GameDashboard(
    val canStartNewRound: Boolean,
    val canPickACard: Boolean,
    val canPass: Boolean,
    val canPlay: Boolean,
    val cardsInHands: List<CardItem>,
    val lastCardPlayed: ImageInfo,
    val playersInfo: CharSequence,
    val gameInfo: String
)