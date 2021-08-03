package ch.qscqlmpa.dwitchstore.ingamestore.model

import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType

data class GameCommonIdAndCurrentRoom(val gameCommonId: GameCommonId, val currentRoom: RoomType)
