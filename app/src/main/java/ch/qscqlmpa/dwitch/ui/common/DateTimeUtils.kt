package ch.qscqlmpa.dwitch.ui.common

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

fun DateTime.toStringEuFormat(): String = toString(DateTimeFormat.forPattern("dd.MM.yyyy HH:mm"))
