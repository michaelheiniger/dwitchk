package ch.qscqlmpa.dwitch.persistence.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitch.model.RoomType

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