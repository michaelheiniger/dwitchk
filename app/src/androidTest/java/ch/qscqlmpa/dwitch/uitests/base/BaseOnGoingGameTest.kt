package ch.qscqlmpa.dwitch.uitests.base

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.matchesWithText
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil
import org.hamcrest.Matchers

abstract class BaseOnGoingGameTest : BaseUiTest() {

    protected val gameName = "LOTR"

    protected val hostName = "Aragorn"

    protected fun assertPlayerInWR(position: Int, playerName: String) {
        onView(ViewAssertionUtil.withRecyclerView(R.id.playerListRw).atPositionOnView(position, R.id.playerNameTv))
            .check(ViewAssertions.matches(ViewMatchers.withText(Matchers.startsWith(playerName))))
    }

    protected fun assertPlayerInWR(position: Int, playerName: String, connectionStateResourceId: Int) {
        assertPlayerInWR(position, playerName)

        onView(ViewAssertionUtil.withRecyclerView(R.id.playerListRw).atPositionOnView(position, R.id.connectionStateTv))
            .check(matchesWithText(connectionStateResourceId))
    }
}