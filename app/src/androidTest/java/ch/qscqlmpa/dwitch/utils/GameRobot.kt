package ch.qscqlmpa.dwitch.utils

import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.game.Game
import org.junit.Assert.assertEquals

class GameRobot(private val gameTest: Game) {

    fun assertId(id: Long): GameRobot {
        assertEquals(gameTest.id, id)
        return this
    }

    fun assertCurrentRoom(currentRoom: RoomType): GameRobot {
        assertEquals(gameTest.currentRoom, currentRoom)
        return this
    }

    fun assertGameCommonId(gameCommonId: Long): GameRobot {
        assertEquals(gameTest.gameCommonId, gameCommonId)
        return this
    }

    fun assertName(name: String): GameRobot {
        assertEquals(gameTest.name, name)
        return this
    }

    fun assertGameState(gameState: String): GameRobot {
        assertEquals(gameTest.gameState, gameState)
        return this
    }

    fun assertLocalPlayerLocalId(localPlayerLocalId: Long): GameRobot {
        assertEquals(gameTest.localPlayerLocalId, localPlayerLocalId)
        return this
    }
}