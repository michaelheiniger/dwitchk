package ch.qscqlmpa.dwitchstore.ingamestore.model

import androidx.room.*
import ch.qscqlmpa.dwitchmodel.game.Game

@Entity(
    tableName = "dwitch_event",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["game_local_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["game_local_id"])]
)
data class DwitchEventStore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "game_local_id") val gameLocalId: Long,
    @ColumnInfo(name = "event") val event: String
)