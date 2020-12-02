package ch.qscqlmpa.dwitchcommunication.utils

import ch.qscqlmpa.dwitchcommunication.model.Envelope
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchmodel.gamediscovery.GameAdvertisingInfo
import kotlinx.serialization.json.Json
import javax.inject.Inject

//@Singleton
class SerializerFactory @Inject constructor(private val json: Json) {

    // Serialize

    fun serialize(gameAdvertisingInfo: GameAdvertisingInfo): String {
        return json.encodeToString(GameAdvertisingInfo.serializer(), gameAdvertisingInfo)
    }

    fun serialize(message: Message): String {
        return json.encodeToString(Message.serializer(), message)
    }

    // Unserialize

    fun unserializeGameInfo(gameInfoAsStr: String): GameAdvertisingInfo {
        return json.decodeFromString(GameAdvertisingInfo.serializer(), gameInfoAsStr)
    }

    fun unserializeEnvelope(envelope: String): Envelope {
        return json.decodeFromString(Envelope.serializer(), envelope)
    }

    fun unserializeMessage(message: String): Message {
        return json.decodeFromString(Message.serializer(), message)
    }
}