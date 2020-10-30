package ch.qscqlmpa.dwitch.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.persistence.typeconverter.*

@Database(
    entities = [
        Game::class,
        Player::class
    ], version = 1, exportSchema = false
)
@TypeConverters(
    RoomTypeTypeConverter::class,
    PlayerStateTypeConverter::class,
    PlayerRoleTypeConverter::class,
    PlayerInGameIdTypeConverter::class,
    GameCommonIdTypeConverter::class
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    abstract fun gameDao(): GameDao
}