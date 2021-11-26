package ch.qscqlmpa.dwitch.ui.ingame.gameroom

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerAction
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerInfo

data class CardInfo(val card: Card, val selectable: Boolean, val selected: Boolean = false)

data class DashboardInfo(

    /**
     * Also defines the order of the players
     */
    val playersInfo: List<PlayerInfo>,

    val localPlayerInfo: LocalPlayerInfo,

    /**
     * Last action performed by a player in the current round.
     */
    val lastPlayerAction: PlayerAction?,

    /**
     * Last card(s) played sitting on the table.
     */
    val lastCardOnTable: PlayedCards?,

    val waitingForPlayerReconnection: Boolean
)

data class LocalPlayerInfo(
    val cardsInHand: List<CardInfo>,
    val canPass: Boolean,
    val canPlay: Boolean
)
