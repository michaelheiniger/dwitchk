package ch.qscqlmpa.dwitchstore.ingamestore.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType

@DatabaseView("SELECT g.id, g.game_common_id as gameCommonId, g.current_room as currentRoom FROM game g")
data class GameCommonIdAndCurrentRoom(
    @ColumnInfo(name = "game_common_id") val gameCommonId: GameCommonId,
    @ColumnInfo(name = "current_room") val currentRoom: RoomType
)