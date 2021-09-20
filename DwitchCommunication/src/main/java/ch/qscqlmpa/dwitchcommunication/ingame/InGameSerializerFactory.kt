package ch.qscqlmpa.dwitchcommunication.ingame

import ch.qscqlmpa.dwitchcommunication.di.InGameCommunicationScope
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import kotlinx.serialization.json.Json
import javax.inject.Inject

@InGameCommunicationScope
class InGameSerializerFactory @Inject constructor(private val json: Json) {

    // Serialize
    fun serialize(message: Message): String {
        return json.encodeToString(Message.serializer(), message)
    }

    // Unserialize
    fun unserializeMessage(message: String): Message {
        return json.decodeFromString(Message.serializer(), message)
    }
}
