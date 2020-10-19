package ch.qscqlmpa.dwitch.persistence.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState

object PlayerStateTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromPlayerStatus(status: PlayerConnectionState): String {
        return status.name
    }

    @JvmStatic
    @TypeConverter
    fun fromString(status: String?): PlayerConnectionState {
        return PlayerConnectionState.valueOf(status!!)
    }
}