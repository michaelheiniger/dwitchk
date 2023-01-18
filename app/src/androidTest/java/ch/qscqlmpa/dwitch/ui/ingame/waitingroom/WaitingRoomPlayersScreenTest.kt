package ch.qscqlmpa.dwitch.ui.ingame.waitingroom

import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import org.junit.Test

class WaitingRoomPlayersScreenTest : BaseUiUnitTest() {

    private val aragorn = PlayerWrUi(10L, name = "Aragorn", connected = true, ready = true)
    private val legolas =
        PlayerWrUi(11L, name = "Legolas", connected = true, ready = false, kickable = true)
    private val gimli =
        PlayerWrUi(12L, name = "Gimli", connected = false, ready = false, kickable = true)
    private val galadriel =
        PlayerWrUi(13L, name = "Galadriel", connected = false, ready = false, kickable = true)
    private val theoden =
        PlayerWrUi(14L, name = "Theoden", connected = true, ready = true, kickable = true)

    @Test
    fun playersAreDisplayed() {
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.players_in_waitingroom))

        assertPlayer(aragorn)
        assertPlayer(legolas)
        assertPlayer(gimli)
        assertPlayer(galadriel)
        assertPlayer(theoden)
    }

    private fun assertPlayer(player: PlayerWrUi) {
        val childNodes = composeTestRule.onNodeWithTag(player.name).onChildren()

        if (player.ready) {
            childNodes.filterToOne(hasText(getString(R.string.ready)))
        } else {
            childNodes.filterToOne(hasText(getString(R.string.not_ready)))
        }

        when (player.connected) {
            true -> childNodes.filterToOne(hasText(getString(R.string.connected_to_host)))
            false -> childNodes.filterToOne(hasText(getString(R.string.disconnected_from_host)))
        }
    }

    private fun launchTest() {
        launchTestWithContent {
            WaitingRoomPlayers(
                players = listOf(aragorn, legolas, gimli, galadriel, theoden)
            )
        }
    }
}
