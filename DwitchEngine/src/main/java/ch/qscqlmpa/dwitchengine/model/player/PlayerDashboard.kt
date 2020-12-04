package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase

data class PlayerDashboard(val localPlayer: Player,
                           val lastCardPlayed: Card,
                           val joker: CardName,
                           val gamePhase: GamePhase,
                           val players: Map<PlayerInGameId, Player>,
                           val playersInPlayingOrder: List<PlayerInGameId>,
                           val cardsInHand: List<Card>,
                           val canPass: Boolean,
                           val canPickACard: Boolean,
                           val canPlay: Boolean,
                           val canStartNewRound: Boolean,
                           val canEndGame: Boolean,
                           val cardsOnTable: List<Card>,
                           val minimumCardValueAllowed: CardName,
                           val gameEvent: GameEvent?
)