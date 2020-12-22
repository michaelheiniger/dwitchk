package ch.qscqlmpa.dwitchstore.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchstore.ingamestore.model.DwitchEventStore
import ch.qscqlmpa.dwitchstore.typeconverter.*


@Database(
    entities = [
        Game::class,
        Player::class,
        DwitchEventStore::class,
    ], version = 1, exportSchema = false
)
@TypeConverters(
    RoomTypeTypeConverter::class,
    PlayerStateTypeConverter::class,
    PlayerRoleTypeConverter::class,
    PlayerInGameIdTypeConverter::class,
    GameCommonIdTypeConverter::class
)
internal abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    abstract fun gameDao(): GameDao

    abstract fun dwitchEventDao(): DwitchEventDao
}