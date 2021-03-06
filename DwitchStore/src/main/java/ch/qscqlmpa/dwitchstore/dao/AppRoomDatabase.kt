package ch.qscqlmpa.dwitchstore.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import ch.qscqlmpa.dwitchstore.typeconverter.*

@Database(
    entities = [
        Game::class,
        Player::class
    ],
    views = [ResumableGameInfo::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(
    RoomTypeTypeConverter::class,
    PlayerRoleTypeConverter::class,
    PlayerDwitchIdTypeConverter::class,
    GameCommonIdTypeConverter::class,
    DateTypeConverter::class
)
internal abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    abstract fun gameDao(): GameDao
}
