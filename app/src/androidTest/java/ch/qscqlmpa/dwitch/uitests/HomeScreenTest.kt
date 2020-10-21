package ch.qscqlmpa.dwitch.uitests


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.gamediscovery.TestNetworkAdapter
import ch.qscqlmpa.dwitch.gamediscovery.network.Packet
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil
import org.hamcrest.Matchers.startsWith
import org.junit.Before
import org.junit.Test


class HomeScreenTest : BaseUiTest() {

    private lateinit var networkListener: TestNetworkAdapter

    @Before
    override fun setup() {
        super.setup()
        testRule.init()
        networkListener = testRule.testAppComponent.testNetworkListener
    }

    @Test
    fun screenIsDisplayed() {
        launch()

        assertControlIsDisplayed(R.id.gameListTv)
        assertControlTextContent(R.id.gameListTv, R.string.ma_game_list_tv)
    }

    @Test
    fun advertisedGameListIsSet() {
        launch()

        networkListener.setPacket(Packet("Game 1", "192.168.1.1", 8890))
        networkListener.setPacket(Packet("Game 2", "192.168.1.2", 8891))

        assertGameInGameList(0, "Game 1 (192.168.1.1)")
        assertGameInGameList(1, "Game 2 (192.168.1.2)")
    }

    private fun assertGameInGameList(position: Int, beginningOfName: String) {
        onView(ViewAssertionUtil.withRecyclerView(R.id.gameListRw)
                .atPositionOnView(position, R.id.gameNameTv))
                .check(matches(withText(startsWith(beginningOfName))))
    }
}
