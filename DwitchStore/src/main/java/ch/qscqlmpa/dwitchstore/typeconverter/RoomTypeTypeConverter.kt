package ch.qscqlmpa.dwitchstore.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitchmodel.game.RoomType

object RoomTypeTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromOperationMode(mode: RoomType): String {
        return mode.name
    }

    @JvmStatic
    @TypeConverter
    fun fromString(mode: String?): RoomType {
        return RoomType.valueOf(mode!!)
    }
}