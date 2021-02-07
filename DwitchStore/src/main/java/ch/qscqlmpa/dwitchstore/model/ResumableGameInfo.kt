package ch.qscqlmpa.dwitchstore.model

import androidx.room.DatabaseView
import androidx.room.Relation
import org.joda.time.DateTime

@DatabaseView(
    """
    SELECT g.id, g.creation_date as creationDate, g.name FROM game g
    WHERE g.game_state is not null
"""
)
data class ResumableGameInfo(
    val id: Long,
    val creationDate: DateTime,
    val name: String,

    @Relation(
        parentColumn = "id",
        entityColumn = "game_local_id",
        entity = Player::class,
        projection = ["name"]
    )
    val playersName: List<String>
)
