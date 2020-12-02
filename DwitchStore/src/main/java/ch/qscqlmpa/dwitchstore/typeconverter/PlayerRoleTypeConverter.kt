package ch.qscqlmpa.dwitchstore.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

object PlayerRoleTypeConverter {
    @JvmStatic
    @TypeConverter
    fun fromPlayerRole(role: PlayerRole): String {
        return role.name
    }

    @JvmStatic
    @TypeConverter
    fun fromString(role: String?): PlayerRole {
        return PlayerRole.valueOf(role!!)
    }
}