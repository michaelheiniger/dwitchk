package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil
import org.hamcrest.Matchers

abstract class BaseOnGoingGameTest : BaseUiTest() {

    protected val gameName = "LOTR"

    protected val hostName = "Aragorn"

    protected fun assertPlayerNameInWR(position: Int, playerName: String) {
        Espresso.onView(ViewAssertionUtil.withRecyclerView(R.id.playerListRw)
                .atPositionOnView(position, R.id.playerNameTv))
                .check(ViewAssertions.matches(ViewMatchers.withText(Matchers.startsWith(playerName))))
    }
}