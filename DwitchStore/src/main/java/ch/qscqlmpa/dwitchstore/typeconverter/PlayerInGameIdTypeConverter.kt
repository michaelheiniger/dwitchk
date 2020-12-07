package ch.qscqlmpa.dwitchstore.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

object PlayerInGameIdTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromPlayerInGameId(playerInGameId: PlayerInGameId): Long {
        return playerInGameId.value
    }

    @JvmStatic
    @TypeConverter
    fun fromLong(id: Long): PlayerInGameId {
        return PlayerInGameId(id)
    }
}