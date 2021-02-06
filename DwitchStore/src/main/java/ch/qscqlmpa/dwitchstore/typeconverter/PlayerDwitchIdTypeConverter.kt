package ch.qscqlmpa.dwitchstore.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

object PlayerDwitchIdTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromPlayerDwitchId(playerDwitchId: PlayerDwitchId): Long {
        return playerDwitchId.value
    }

    @JvmStatic
    @TypeConverter
    fun fromLong(id: Long): PlayerDwitchId {
        return PlayerDwitchId(id)
    }
}
