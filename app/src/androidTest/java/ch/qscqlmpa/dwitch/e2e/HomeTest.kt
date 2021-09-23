package ch.qscqlmpa.dwitch.e2e

import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.e2e.base.BaseE2eTest
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.junit.Test
import java.util.*

class HomeTest : BaseE2eTest() {

    @Test
    fun screenIsDisplayed() {
        assertCurrentScreenIsHomeSreen()

        advertiseGame1()
        advertiseGame2()

        testRule.assertTextIsDisplayedOnce("Game 1")
        testRule.assertTextIsDisplayedOnce("Game 2")
    }

    private fun advertiseGame1() {
        advertiseGame(
            isNew = true,
            gameName = "Game 1",
            gameCommonId = GameCommonId(UUID.randomUUID()),
            gamePort = 8890,
            gameIpAddress = "192.168.1.1",
            senderPort = 2454
        )
    }

    private fun advertiseGame2() {
        advertiseGame(
            isNew = true,
            gameName = "Game 2",
            gameCommonId = GameCommonId(UUID.randomUUID()),
            gamePort = 8891,
            gameIpAddress = "192.168.1.2",
            senderPort = 6543
        )
    }
}
