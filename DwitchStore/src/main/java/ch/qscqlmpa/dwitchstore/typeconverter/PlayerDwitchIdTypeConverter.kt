package ch.qscqlmpa.dwitchstore.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

object PlayerDwitchIdTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromPlayerDwitchId(dwitchPlayerId: DwitchPlayerId): Long {
        return dwitchPlayerId.value
    }

    @JvmStatic
    @TypeConverter
    fun fromLong(id: Long): DwitchPlayerId {
        return DwitchPlayerId(id)
    }
}
