package ch.qscqlmpa.dwitchengine.model.info

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

data class GameInfo(
    val currentPlayerId: PlayerDwitchId,
    val playerInfos: Map<PlayerDwitchId, PlayerInfo>,
    val gamePhase: GamePhase,
    val playingOrder: List<PlayerDwitchId>,
    val joker: CardName,
    val lastCardPlayed: Card,
    val cardsOnTable: List<Card>,
    val gameEvent: GameEvent?
)  {
    fun getCurrentPlayer(): PlayerInfo {
        return playerInfos.getValue(currentPlayerId)
    }

    fun getDwitchedPlayer(): PlayerInfo? {
        return playerInfos.values.find { p -> p.dwitched }
    }
}