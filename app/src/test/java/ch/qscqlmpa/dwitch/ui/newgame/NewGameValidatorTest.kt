package ch.qscqlmpa.dwitch.ui.newgame

import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameError
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameValidator
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class NewGameValidatorTest {

    @Test
    fun validate_success() {

        val gameName = "Round Table Knights Game"
        val playerName = "Arthur"

        val errorListTest = NewGameValidator.validate(gameName, playerName)

        assertTrue(errorListTest.isEmpty())
    }

    @Test
    fun validate_playerNameIsEmpty() {

        val errorListRef = listOf(NewGameError.PLAYER_NAME_IS_EMPTY)

        val gameName = "Round Table Knights Game"
        val playerName = ""

        val errorListTest = NewGameValidator.validate(gameName, playerName)

        assertEquals(errorListRef, errorListTest)
    }

    @Test
    fun validate_gameNameIsEmpty() {

        val errorListRef = listOf(NewGameError.GAME_NAME_IS_EMPTY)

        val gameName = ""
        val playerName = "Arthur"

        val errorListTest = NewGameValidator.validate(gameName, playerName)

        assertEquals(errorListRef, errorListTest)
    }

}