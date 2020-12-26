package ch.qscqlmpa.dwitchengine.model.info

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

data class GameInfo(
    val playerInfos: Map<PlayerInGameId, PlayerInfo>,
    val gamePhase: GamePhase,
    val playingOrder: List<PlayerInGameId>,
    val joker: CardName,
    val lastCardPlayed: Card,
    val cardsOnTable: List<Card>,
    val gameEvent: GameEvent?
)