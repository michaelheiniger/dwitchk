package ch.qscqlmpa.dwitchmodel.game

import androidx.room.DatabaseView
import androidx.room.Relation
import ch.qscqlmpa.dwitchmodel.player.Player

@DatabaseView("""
    SELECT g.id, g.name FROM game g
    WHERE g.game_state is not null
""")
data class ResumableGameInfo(
    val id: Long,
    val name: String,

    @Relation(
        parentColumn = "id",
        entityColumn = "game_local_id",
        entity = Player::class,
        projection = ["name"]
    )
    val playersName: List<String>
)