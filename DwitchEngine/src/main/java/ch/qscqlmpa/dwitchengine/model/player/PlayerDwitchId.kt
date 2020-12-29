package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PlayerDwitchIdSerializer::class)
data class PlayerDwitchId(val value: Long) : Comparable<PlayerDwitchId> {

    override fun compareTo(other: PlayerDwitchId): Int {
        return value.compareTo(other.value)
    }
}

object PlayerDwitchIdSerializer : KSerializer<PlayerDwitchId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PlayerDwitchId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: PlayerDwitchId) {
        encoder.encodeLong(value.value)
    }

    override fun deserialize(decoder: Decoder): PlayerDwitchId {
        return PlayerDwitchId(decoder.decodeLong())
    }
}