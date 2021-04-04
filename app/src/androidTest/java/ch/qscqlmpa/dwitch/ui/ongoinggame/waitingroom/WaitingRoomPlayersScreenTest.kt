package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.compose.ui.test.*
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
    private val theoden = PlayerWrUi(name = "Theoden", connectionState = PlayerConnectionState.CONNECTED, ready = false)

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
        val aragornChildNodes = composeTestRule.onNode(hasTestTag(player.name)).onChildren()
        aragornChildNodes.filterToOne(hasText(player.name))
        aragornChildNodes.filterToOne(hasText(getString(R.string.ready)))

        val readyCheckboxTag = "readyCheckbox"
        aragornChildNodes.filterToOne(hasTestTag(readyCheckboxTag))
            .assertIsNotEnabled()
            .assertIsDisplayed()
        if (player.ready) {
            aragornChildNodes.filterToOne(hasTestTag(readyCheckboxTag)).assertIsOn()
        } else {
            aragornChildNodes.filterToOne(hasTestTag(readyCheckboxTag)).assertIsOff()
        }

        when (player.connectionState) {
            PlayerConnectionState.CONNECTED -> aragornChildNodes.filterToOne(hasText(getString(R.string.connected_to_host)))
            PlayerConnectionState.DISCONNECTED -> aragornChildNodes.filterToOne(hasText(getString(R.string.disconnected_from_host)))
        }
    }

    private fun launchTest() {
        launchTestWithContent {
            WaitingRoomPlayersScreen(players = listOf(aragorn, legolas, gimli, galadriel, theoden))
        }
    }
}