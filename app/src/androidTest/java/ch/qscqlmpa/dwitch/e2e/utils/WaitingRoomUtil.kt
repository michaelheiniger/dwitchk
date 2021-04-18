package ch.qscqlmpa.dwitch.e2e.utils

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import ch.qscqlmpa.dwitch.R

object WaitingRoomUtil {

    const val PLAYER_CONNECTED = R.string.player_connected
    const val PLAYER_DISCONNECTED = R.string.player_disconnected

    //
    fun ComposeContentTestRule.assertPlayerInWr(name: String): SemanticsNodeInteraction {
        return onNode(hasTestTag(name))
    }

    fun ComposeContentTestRule.assertPlayerInWr(name: String, ready: Boolean): SemanticsNodeInteraction {
        val sni = assertPlayerInWr(name)
        if (ready) sni.onChildren().filterToOne(hasText("Ready")) else sni.onChildren()
            .filterToOne(hasText("Not ready")) //TODO: i18n proof
        return sni
    }

    fun ComposeContentTestRule.assertPlayerInWr(name: String, connectionState: String): SemanticsNodeInteraction {
        val sni = assertPlayerInWr(name)
        sni.onChildren().filterToOne(hasText(connectionState)).assertIsDisplayed()
        return sni
    }

//
//    fun assertPlayerInWr(position: Int, name: String, ready: Boolean, connectionState: Int) {
//        assertPlayerInWr(position, name)
//        assertPlayerConnected(position, connectionState)
//        assertPlayerReady(position, ready)
//    }
//
//    private fun assertPlayerReady(position: Int, ready: Boolean) {
//        if (ready) {
//            onView(withRecyclerView(R.id.playerListRw).atPositionOnView(position, R.id.playerReadyCkb))
//                .check(ViewAssertions.matches(ViewMatchers.isChecked()))
//        } else {
//            onView(withRecyclerView(R.id.playerListRw).atPositionOnView(position, R.id.playerReadyCkb))
//                .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isChecked())))
//        }
//    }
//
//    private fun assertPlayerConnected(position: Int, connectionStateResourceId: Int) {
//        onView(withRecyclerView(R.id.playerListRw).atPositionOnView(position, R.id.connectionStateTv))
//            .check(UiUtil.matchesWithText(connectionStateResourceId))
//    }
}
