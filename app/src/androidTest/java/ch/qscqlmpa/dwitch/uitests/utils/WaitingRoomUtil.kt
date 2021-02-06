package ch.qscqlmpa.dwitch.uitests.utils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.assertRecyclerViewElementText
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil.withRecyclerView
import org.hamcrest.CoreMatchers

object WaitingRoomUtil {

    const val PLAYER_CONNECTED = R.string.player_connected
    const val PLAYER_DISCONNECTED = R.string.player_disconnected

    fun assertPlayerInWr(position: Int, name: String) {
        assertRecyclerViewElementText(R.id.playerListRw, R.id.playerNameTv, position, name)
    }

    fun assertPlayerInWr(position: Int, name: String, ready: Boolean) {
        assertPlayerInWr(position, name)
        assertPlayerReady(position, ready)
    }

    fun assertPlayerInWr(position: Int, name: String, connectionStateResourceId: Int) {
        assertPlayerInWr(position, name)
        assertPlayerConnected(position, connectionStateResourceId)
    }

    fun assertPlayerInWr(position: Int, name: String, ready: Boolean, connectionState: Int) {
        assertPlayerInWr(position, name)
        assertPlayerConnected(position, connectionState)
        assertPlayerReady(position, ready)
    }

    private fun assertPlayerReady(position: Int, ready: Boolean) {
        if (ready) {
            onView(withRecyclerView(R.id.playerListRw).atPositionOnView(position, R.id.playerReadyCkb))
                .check(ViewAssertions.matches(ViewMatchers.isChecked()))
        } else {
            onView(withRecyclerView(R.id.playerListRw).atPositionOnView(position, R.id.playerReadyCkb))
                .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isChecked())))
        }
    }

    private fun assertPlayerConnected(position: Int, connectionStateResourceId: Int) {
        onView(withRecyclerView(R.id.playerListRw).atPositionOnView(position, R.id.connectionStateTv))
            .check(UiUtil.matchesWithText(connectionStateResourceId))
    }
}
