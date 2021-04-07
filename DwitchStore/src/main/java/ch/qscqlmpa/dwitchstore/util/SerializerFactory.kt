package ch.qscqlmpa.dwitchstore.util

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardId
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardSuit
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
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

    fun serialize(gamePhase: DwitchGamePhase): String {
        return json.encodeToString(DwitchGamePhase.serializer(), gamePhase)
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

    fun serialize(rank: DwitchRank): String {
        return json.encodeToString(DwitchRank.serializer(), rank)
    }

    fun serialize(gameState: DwitchGameState): String {
        return json.encodeToString(DwitchGameState.serializer(), gameState)
    }

    fun serialize(player: DwitchPlayer): String {
        return json.encodeToString(DwitchPlayer.serializer(), player)
    }

    fun serialize(dwitchGameEvent: DwitchGameEvent): String {
        return json.encodeToString(DwitchGameEvent.serializer(), dwitchGameEvent)
    }

    fun serialize(dwitchPlayerId: DwitchPlayerId): String {
        return json.encodeToString(DwitchPlayerId.serializer(), dwitchPlayerId)
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

    fun unserializeRank(rank: String): DwitchRank {
        return json.decodeFromString(DwitchRank.serializer(), rank)
    }

    fun unserializeGameState(gameState: String): DwitchGameState {
        return json.decodeFromString(DwitchGameState.serializer(), gameState)
    }

    fun unserializePlayer(player: String): DwitchPlayer {
        return json.decodeFromString(DwitchPlayer.serializer(), player)
    }

    fun unserializeGameEvent(gameEvent: String): DwitchGameEvent {
        return json.decodeFromString(DwitchGameEvent.serializer(), gameEvent)
    }

    fun unserializePlayerDwitchId(playerDwitchId: String): DwitchPlayerId {
        return json.decodeFromString(DwitchPlayerId.serializer(), playerDwitchId)
    }

    fun unserializeCards(cards: String): List<Card> {
        return json.decodeFromString(ListSerializer(Card.serializer()), cards)
    }
}
