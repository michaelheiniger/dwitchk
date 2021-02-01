package ch.qscqlmpa.dwitchstore.typeconverter

import androidx.room.TypeConverter
import org.joda.time.DateTime

object DateTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromDateTime(date: DateTime): String {
        return date.toString()
    }

    @JvmStatic
    @TypeConverter
    fun fromString(date: String): DateTime {
        return DateTime.parse(date)
    }
}