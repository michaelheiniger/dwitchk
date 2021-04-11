package ch.qscqlmpa.dwitchengine.model.info

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

data class DwitchGameInfo(
    val currentPlayerId: DwitchPlayerId,
    val playerInfos: Map<DwitchPlayerId, DwitchPlayerInfo>,
    val gamePhase: DwitchGamePhase,
    val playingOrder: List<DwitchPlayerId>,
    val joker: CardName,
    val lastCardPlayed: Card,
    val cardsOnTable: List<Card>,
    val dwitchGameEvent: DwitchGameEvent?
) {
    val playerInfosList: List<DwitchPlayerInfo> by lazy { playerInfos.values.toList() }

    fun getCurrentPlayer(): DwitchPlayerInfo {
        return playerInfos.getValue(currentPlayerId)
    }

    fun getDwitchedPlayer(): DwitchPlayerInfo? {
        return playerInfos.values.find { p -> p.dwitched }
    }
}
