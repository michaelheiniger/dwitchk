package ch.qscqlmpa.dwitch.ongoinggame.communication.serialization

import ch.qscqlmpa.dwitch.gameadvertising.GameInfo
import ch.qscqlmpa.dwitch.ongoinggame.communication.Envelope
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardId
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardSuit
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.Rank
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SerializerFactory @Inject constructor(private val json: Json) {

    // Serialize

    fun serialize(gameInfo: GameInfo): String {
        return json.encodeToString(GameInfo.serializer(), gameInfo)
    }

    fun serialize(message: Message): String {
        return json.encodeToString(Message.serializer(), message)
    }

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

    fun serialize(playerInGameId: PlayerInGameId): String {
        return json.encodeToString(PlayerInGameId.serializer(), playerInGameId)
    }

    // Unserialize

    fun unserializeGameInfo(gameInfoAsStr: String): GameInfo {
        return json.decodeFromString(GameInfo.serializer(), gameInfoAsStr)
    }

    fun unserializeEnvelope(envelope: String): Envelope {
        return json.decodeFromString(Envelope.serializer(), envelope)
    }

    fun unserializeMessage(message: String): Message {
        return json.decodeFromString(Message.serializer(), message)
    }

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

    fun unserializePlayerInGameId(playerInGameId: String): PlayerInGameId {
        return json.decodeFromString(PlayerInGameId.serializer(), playerInGameId)
    }
}

