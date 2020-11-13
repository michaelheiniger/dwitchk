package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.gamediscovery.TestNetworkAdapter
import ch.qscqlmpa.dwitch.gamediscovery.network.Packet
import ch.qscqlmpa.dwitch.uitests.base.BaseUiTest
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.matchesWithErrorText
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.matchesWithText
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil.withRecyclerView
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Test


class NewGameAsGuestTest : BaseUiTest() {

    private lateinit var networkListener: TestNetworkAdapter
    private val message1 = "{\"gameCommonId\":{\"value\":23},\"gameName\":\"Kaamelott\",\"gamePort\":8889}"
    private val message2 = "{\"gameCommonId\":{\"value\":65},\"gameName\":\"Les Bronzés\",\"gamePort\":8890}"
    private val packet1 = Packet(message1, "192.168.1.1", 3456)
    private val packet2 = Packet(message2, "192.168.1.2", 7657)

    @Before
    override fun setup() {
        super.setup()
        testRule.init()
        networkListener = testRule.testAppComponent.testNetworkListener
    }

    @Test
    fun joinGame() {
        launch()

        networkListener.setPacket(packet1)
        networkListener.setPacket(packet2)

        onView(withRecyclerView(R.id.gameListRw).atPositionOnView(1, R.id.gameNameTv))
                .perform(click())

        onView(withId(R.id.playerNameEdt)).perform(replaceText("Bernard Morin"))

        onView(withId(R.id.playerNameEdt)).check(matches(withText("Bernard Morin")))
        onView(withId(R.id.gameNameEdt)).check(matches(withText("Les Bronzés")))
        onView(withId(R.id.gameNameEdt)).check(matches(not(isEnabled())))
    }

    @Test
    fun joinGame_validationFailed() {
        launch()

        networkListener.setPacket(packet1)
        networkListener.setPacket(packet2)

        onView(withRecyclerView(R.id.gameListRw).atPositionOnView(1, R.id.gameNameTv))
                .perform(click())

        onView(withId(R.id.playerNameEdt)).perform(replaceText(""));

        onView(withId(R.id.nextBtn)).perform(click())

        onView(withId(R.id.playerNameEdt)).check(matchesWithErrorText(R.string.nge_player_name_empty))
    }

    @Test
    fun abortJoinGame() {
        launch()

        networkListener.setPacket(packet1)
        networkListener.setPacket(packet2)

        onView(withRecyclerView(R.id.gameListRw)
                .atPositionOnView(1, R.id.gameNameTv))
                .perform(click())

        onView(withId(R.id.playerNameEdt)).perform(replaceText("Bernard Morin"))

        onView(withId(R.id.backBtn)).perform(click())

        onView(withId(R.id.gameListTv)).check(matchesWithText(R.string.ma_game_list_tv))
    }
}
