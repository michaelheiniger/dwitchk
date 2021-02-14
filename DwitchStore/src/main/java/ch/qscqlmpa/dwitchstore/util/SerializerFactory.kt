package ch.qscqlmpa.dwitchstore.util

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardId
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardSuit
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreScope
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject

@InGameStoreScope
class SerializerFactory @Inject constructor(private val json: Json) {

    // Serialize

    fun serialize(card: Card): String {
        return json.encodeToString(Card.serializer(), card)
    }

    fun serialize(gamePhase: GamePhase): String {
        return json.encodeToString(GamePhase.serializer(), gamePhase)
    }

    fun serialize(cardId: CardId): String {
        return json.encodeToString(CardId.serializer(), cardId)
    }

    fun serialize(cardName: CardName): String {
        return json.encodeToString(CardName.serializer(), cardName)
    }

    fun serialize(cardSuit: CardSuit): String {
        return json.encodeToString(CardSuit.serializer(), cardSuit)
    }

    fun serialize(rank: Rank): String {
        return json.encodeToString(Rank.serializer(), rank)
    }

    fun serialize(gameState: GameState): String {
        return json.encodeToString(GameState.serializer(), gameState)
    }

    fun serialize(player: Player): String {
        return json.encodeToString(Player.serializer(), player)
    }

    fun serialize(gameEvent: GameEvent): String {
        return json.encodeToString(GameEvent.serializer(), gameEvent)
    }

    fun serialize(playerDwitchId: PlayerDwitchId): String {
        return json.encodeToString(PlayerDwitchId.serializer(), playerDwitchId)
    }

    fun serialize(cards: List<Card>): String {
        return json.encodeToString(ListSerializer(Card.serializer()), cards)
    }

    // Unserialize

    fun unserializeCard(card: String): Card {
        return json.decodeFromString(Card.serializer(), card)
    }

    fun unserializeCardName(cardName: String): CardName {
        return json.decodeFromString(CardName.serializer(), cardName)
    }

    fun unserializeCardSuit(cardSuit: String): CardSuit {
        return json.decodeFromString(CardSuit.serializer(), cardSuit)
    }

    fun unserializeRank(rank: String): Rank {
        return json.decodeFromString(Rank.serializer(), rank)
    }

    fun unserializeGameState(gameState: String): GameState {
        return json.decodeFromString(GameState.serializer(), gameState)
    }

    fun unserializePlayer(player: String): Player {
        return json.decodeFromString(Player.serializer(), player)
    }

    fun unserializeGameEvent(gameEvent: String): GameEvent {
        return json.decodeFromString(GameEvent.serializer(), gameEvent)
    }

    fun unserializePlayerDwitchId(playerDwitchId: String): PlayerDwitchId {
        return json.decodeFromString(PlayerDwitchId.serializer(), playerDwitchId)
    }

    fun unserializeCards(cards: String): List<Card> {
        return json.decodeFromString(ListSerializer(Card.serializer()), cards)
    }
}
