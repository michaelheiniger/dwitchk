package ch.qscqlmpa.dwitchstore.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

object GameCommonIdTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromGameCommonId(gameCommonId: GameCommonId): Long {
        return gameCommonId.value
    }

    @JvmStatic
    @TypeConverter
    fun fromLong(id: Long): GameCommonId {
        return GameCommonId(id)
    }
}