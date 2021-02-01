package ch.qscqlmpa.dwitchstore.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.ResumableGameInfo
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchstore.typeconverter.*


@Database(
    entities = [
        Game::class,
        Player::class
    ],
    views = [ResumableGameInfo::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    RoomTypeTypeConverter::class,
    PlayerStateTypeConverter::class,
    PlayerRoleTypeConverter::class,
    PlayerDwitchIdTypeConverter::class,
    GameCommonIdTypeConverter::class,
    DateTypeConverter::class
)
internal abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    abstract fun gameDao(): GameDao
}