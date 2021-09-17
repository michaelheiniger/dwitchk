package ch.qscqlmpa.dwitchmodel.game

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

@Serializable(with = GameCommonIdSerializer::class)
data class GameCommonId(val value: UUID)

object GameCommonIdSerializer : KSerializer<GameCommonId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GameCommonId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GameCommonId) {
        encoder.encodeString(value.value.toString())
    }

    override fun deserialize(decoder: Decoder): GameCommonId {
        return GameCommonId(UUID.fromString(decoder.decodeString()))
    }
}

