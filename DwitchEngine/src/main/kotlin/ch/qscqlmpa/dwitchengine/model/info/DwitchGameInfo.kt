package ch.qscqlmpa.dwitchengine.model.info

import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

data class DwitchGameInfo(
    val currentPlayerId: DwitchPlayerId,
    val playerInfos: Map<DwitchPlayerId, DwitchPlayerInfo>,
    val gamePhase: DwitchGamePhase,
    val playingOrder: List<DwitchPlayerId>,
    val joker: CardName,
    val lastCardPlayed: PlayedCards?,
    val cardsOnTable: List<PlayedCards>,
    val dwitchGameEvent: DwitchGameEvent?,
    val newRoundCanBeStarted: Boolean
)
