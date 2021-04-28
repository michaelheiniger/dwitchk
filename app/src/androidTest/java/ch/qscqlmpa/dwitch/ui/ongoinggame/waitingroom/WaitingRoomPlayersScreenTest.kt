package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import org.junit.Test

class WaitingRoomPlayersScreenTest : BaseUiUnitTest() {

    private val aragorn = PlayerWrUi(name = "Aragorn", connectionState = PlayerConnectionState.CONNECTED, ready = true)
    private val legolas = PlayerWrUi(name = "Legolas", connectionState = PlayerConnectionState.CONNECTED, ready = false)
    private val gimli = PlayerWrUi(name = "Gimli", connectionState = PlayerConnectionState.DISCONNECTED, ready = false)
    private val galadriel = PlayerWrUi(name = "Galadriel", connectionState = PlayerConnectionState.DISCONNECTED, ready = false)
    private val theoden = PlayerWrUi(name = "Theoden", connectionState = PlayerConnectionState.CONNECTED, ready = true)

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

        when (player.connectionState) {
            PlayerConnectionState.CONNECTED -> childNodes.filterToOne(hasText(getString(R.string.connected_to_host)))
            PlayerConnectionState.DISCONNECTED -> childNodes.filterToOne(hasText(getString(R.string.disconnected_from_host)))
        }
    }

    private fun launchTest() {
        launchTestWithContent {
            WaitingRoomPlayersScreen(
                players = listOf(aragorn, legolas, gimli, galadriel, theoden),
                showAddComputerPlayer = true,
                onAddComputerPlayer = {},
            )
        }
    }
}
