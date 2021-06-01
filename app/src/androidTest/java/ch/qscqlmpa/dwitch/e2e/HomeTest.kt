package ch.qscqlmpa.dwitch.e2e

import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.e2e.base.BaseUiTest
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.junit.Test

class HomeTest : BaseUiTest() {

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
            gameCommonId = GameCommonId(23),
            gamePort = 8890,
            senderIpAddress = "192.168.1.1",
            senderPort = 2454
        )
    }

    private fun advertiseGame2() {
        advertiseGame(
            isNew = true,
            gameName = "Game 2",
            gameCommonId = GameCommonId(65),
            gamePort = 8891,
            senderIpAddress = "192.168.1.2",
            senderPort = 6543
        )
    }
}
