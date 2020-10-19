package ch.qscqlmpa.dwitch.persistence.typeconverter

import androidx.room.TypeConverter
import ch.qscqlmpa.dwitch.model.player.PlayerRole

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