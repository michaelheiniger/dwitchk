package ch.qscqlmpa.dwitchstore.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import java.util.*

object GameCommonIdTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromGameCommonId(gameCommonId: GameCommonId): String {
        return gameCommonId.value.toString()
    }

    @JvmStatic
    @TypeConverter
    fun fromLong(uuidAsString: String): GameCommonId {
        return GameCommonId(UUID.fromString(uuidAsString))
    }
}
