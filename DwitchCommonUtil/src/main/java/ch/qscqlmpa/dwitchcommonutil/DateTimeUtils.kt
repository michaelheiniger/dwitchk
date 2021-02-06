package ch.qscqlmpa.dwitchcommonutil

import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

object DateTimeUtils {

    private const val TIME_FORMAT = "HH:mm:ss"

    private val dateTimeFormatter = DateTimeFormat.forPattern(TIME_FORMAT)

    fun parseLocalTime(hour: String, min: String, sec: String): LocalTime {
        return dateTimeFormatter.parseLocalTime(String.format("%s:%s:%s", hour, min, sec))
    }
}
