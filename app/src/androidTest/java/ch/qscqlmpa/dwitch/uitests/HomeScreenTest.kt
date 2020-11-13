package ch.qscqlmpa.dwitch.uitests


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.gamediscovery.TestNetworkAdapter
import ch.qscqlmpa.dwitch.gamediscovery.network.Packet
import ch.qscqlmpa.dwitch.uitests.base.BaseUiTest
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil.withRecyclerView
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

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun advertisedGameListIsSet() {
        launch()

        val message1 = "{\"gameCommonId\":{\"value\":23},\"gameName\":\"Game 1\",\"gamePort\":8890}"
        val message2 = "{\"gameCommonId\":{\"value\":65},\"gameName\":\"Game 2\",\"gamePort\":8891}"

        networkListener.setPacket(Packet(message1, "192.168.1.1", 2454))
        networkListener.setPacket(Packet(message2, "192.168.1.2", 6543))

        assertGameInGameList(0, "Game 1 (192.168.1.1)")
        assertGameInGameList(1, "Game 2 (192.168.1.2)")
    }

    private fun assertGameInGameList(position: Int, beginningOfName: String) {
        onView(withRecyclerView(R.id.gameListRw)
                .atPositionOnView(position, R.id.gameNameTv))
                .check(matches(withText(startsWith(beginningOfName))))
    }
}
