package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.PlayerInfo

data class CardInfo(val card: Card, val selectable: Boolean, val selected: Boolean = false)

data class DashboardInfo(

    /**
     * Also defines the order of the players
     */
    val playersInfo: List<PlayerInfo>,

    val localPlayerInfo: LocalPlayerInfo,

    /**
     * Last card(s) played sitting on the table.
     */
    val lastCardPlayed: PlayedCards?
)

data class LocalPlayerInfo(
    val cardsInHand: List<CardInfo>,
    val canPass: Boolean,
    val canPlay: Boolean
)