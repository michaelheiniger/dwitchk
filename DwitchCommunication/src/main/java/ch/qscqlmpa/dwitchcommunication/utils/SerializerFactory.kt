package ch.qscqlmpa.dwitchcommunication.utils

import ch.qscqlmpa.dwitchcommunication.di.InGameCommunicationScope
import ch.qscqlmpa.dwitchcommunication.model.Message
import kotlinx.serialization.json.Json
import javax.inject.Inject

@InGameCommunicationScope
class SerializerFactory @Inject constructor(private val json: Json) {

    // Serialize

//    fun serialize(gameAdvertisingInfo: ch.qscqlmpa.dwitchgame.GameAdvertisingInfo): String {
//        return json.encodeToString(ch.qscqlmpa.dwitchgame.GameAdvertisingInfo.serializer(), gameAdvertisingInfo)
//    }

    fun serialize(message: Message): String {
        return json.encodeToString(Message.serializer(), message)
    }

    // Unserialize

//    fun unserializeGameInfo(gameInfoAsStr: String): ch.qscqlmpa.dwitchgame.GameAdvertisingInfo {
//        return json.decodeFromString(ch.qscqlmpa.dwitchgame.GameAdvertisingInfo.serializer(), gameInfoAsStr)
//    }

    fun unserializeMessage(message: String): Message {
        return json.decodeFromString(Message.serializer(), message)
    }
}
