package ch.qscqlmpa.dwitchcommunication.common

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SerializerFactory @Inject constructor(private val json: Json) {

    // Serialize
    fun serialize(gameAdvertisingInfo: GameAdvertisingInfo): String {
        return json.encodeToString(GameAdvertisingInfo.serializer(), gameAdvertisingInfo)
    }

    // Unserialize
    fun unserializeGameInfo(gameInfoAsStr: String): GameAdvertisingInfo {
        return json.decodeFromString(GameAdvertisingInfo.serializer(), gameInfoAsStr)
    }
}
