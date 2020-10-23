package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PlayerInGameIdSerializer::class)
data class PlayerInGameId(val value: Long) : Comparable<PlayerInGameId> {

    override fun compareTo(other: PlayerInGameId): Int {
        return value.compareTo(other.value)
    }
}

object PlayerInGameIdSerializer : KSerializer<PlayerInGameId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PlayerInGameId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: PlayerInGameId) {
        encoder.encodeLong(value.value)
    }

    override fun deserialize(decoder: Decoder): PlayerInGameId {
        return PlayerInGameId(decoder.decodeLong())
    }
}