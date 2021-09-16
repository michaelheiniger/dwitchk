package ch.qscqlmpa.dwitchstore.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import ch.qscqlmpa.dwitchstore.typeconverter.DateTypeConverter
import ch.qscqlmpa.dwitchstore.typeconverter.GameCommonIdTypeConverter
import ch.qscqlmpa.dwitchstore.typeconverter.PlayerDwitchIdTypeConverter
import ch.qscqlmpa.dwitchstore.typeconverter.PlayerRoleTypeConverter

@Database(
    entities = [
        Game::class,
        Player::class
    ],
    views = [ResumableGameInfo::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(
    PlayerRoleTypeConverter::class,
    PlayerDwitchIdTypeConverter::class,
    GameCommonIdTypeConverter::class,
    DateTypeConverter::class
)
internal abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    abstract fun gameDao(): GameDao
}
