package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.UiUtil.matchesWithErrorText
import ch.qscqlmpa.dwitch.uitests.UiUtil.matchesWithText
import org.junit.Before
import org.junit.Test


class NewGameAsHostTest : BaseUiTest() {

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun createGame() {
        launch()

        val gameName = "Les Bronzés font du ski"
        val playerName = "Jean-Claude Duss"

        onView(withId(R.id.createGameBtn)).perform(click())

        onView(withId(R.id.playerNameEdt)).perform(replaceText(playerName))
        onView(withId(R.id.gameNameEdt)).perform(replaceText(gameName))

        onView(withId(R.id.playerNameEdt)).check(matches(withText(playerName)))
        onView(withId(R.id.gameNameEdt)).check(matches(withText(gameName)))
        onView(withId(R.id.gameNameEdt)).check(matches(isEnabled()))
    }

    @Test
    fun createGame_validationOfPlayerNameFailed() {
        launch()

        onView(withId(R.id.createGameBtn)).perform(click())

        onView(withId(R.id.playerNameEdt)).perform(replaceText(""))
        onView(withId(R.id.gameNameEdt)).perform(replaceText("Les Bronzés font du ski"))

        onView(withId(R.id.nextBtn)).perform(click())

        onView(withId(R.id.playerNameEdt)).check(matchesWithErrorText(R.string.nge_player_name_empty))
    }

    @Test
    fun createGame_validationOfGameNameFailed() {
        launch()

        onView(withId(R.id.createGameBtn)).perform(click())

        onView(withId(R.id.playerNameEdt)).perform(replaceText("Jean-Claude Duss"))
        onView(withId(R.id.gameNameEdt)).perform(replaceText(""))

        onView(withId(R.id.nextBtn)).perform(click())

        onView(withId(R.id.gameNameEdt)).check(matchesWithErrorText(R.string.nge_game_name_empty))
    }

    @Test
    fun abortCreateGame() {
        launch()

        onView(withId(R.id.createGameBtn)).perform(click())

        onView(withId(R.id.playerNameTv)).check(matchesWithText(R.string.nga_player_name_tv))
        onView(withId(R.id.gameNameTv)).check(matchesWithText(R.string.nga_game_name_tv))

        onView(withId(R.id.backBtn)).perform(click())

        onView(withId(R.id.gameListTv)).check(matchesWithText(R.string.ma_game_list_tv))
    }
}
