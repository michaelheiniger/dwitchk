package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PlayerDwitchIdSerializer::class)
data class DwitchPlayerId(val value: Long) : Comparable<DwitchPlayerId> {

    override fun compareTo(other: DwitchPlayerId): Int {
        return value.compareTo(other.value)
    }
}

object PlayerDwitchIdSerializer : KSerializer<DwitchPlayerId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PlayerDwitchId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: DwitchPlayerId) {
        encoder.encodeLong(value.value)
    }

    override fun deserialize(decoder: Decoder): DwitchPlayerId {
        return DwitchPlayerId(decoder.decodeLong())
    }
}
