package ch.qscqlmpa.dwitch.e2e.utils

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.UiTags

object WaitingRoomUtil {

    const val PLAYER_CONNECTED = R.string.player_connected
    const val PLAYER_DISCONNECTED = R.string.player_disconnected

    fun ComposeContentTestRule.assertPlayerInWr(name: String): SemanticsNodeInteraction {
        return onNodeWithTag(name, useUnmergedTree = true).assertExists("Player $name is not in WR")
    }

    fun ComposeContentTestRule.assertPlayerInWr(name: String, ready: Boolean): SemanticsNodeInteraction {
        val sni = assertPlayerInWr(name)
        if (ready) sni.onChildren().filterToOne(hasText("Ready")) else sni.onChildren()
            .filterToOne(hasText("Not ready")) // TODO: i18n proof
        return sni
    }

    fun ComposeContentTestRule.assertPlayerInWr(name: String, connectionState: String): SemanticsNodeInteraction {
        val sni = assertPlayerInWr(name)
        sni.onChildren().filterToOne(hasText(connectionState)).assertIsDisplayed()
        return sni
    }

    fun ComposeContentTestRule.assertPlayerInWr(name: String, connectionState: String, ready: Boolean): SemanticsNodeInteraction {
        assertPlayerInWr(name, connectionState)
        return assertPlayerInWr(name, ready)
    }

    fun ComposeContentTestRule.assertLaunchGameControlIsEnabled() {
        onNodeWithTag(UiTags.launchGameControl)
            .assertIsEnabled()
            .assertIsDisplayed()
    }

    fun ComposeContentTestRule.launchGame() {
        onNodeWithTag(UiTags.launchGameControl).performClick()
    }
}
