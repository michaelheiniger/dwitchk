package ch.qscqlmpa.dwitch.ui.home.home

import androidx.compose.ui.test.onNodeWithTag
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayed
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class HomeScreenTest : BaseUiUnitTest() {

    private lateinit var advertisedGames: LoadedData<List<AdvertisedGame>>
    private lateinit var resumableGames: LoadedData<List<ResumableGameInfo>>

    @Before
    fun setup() {
        advertisedGames = LoadedData.Loading
        resumableGames = LoadedData.Loading
    }

    @Test
    fun advertisedGamesAreSuccesfullyDisplayed() {
        advertisedGames = LoadedData.Success(
            listOf(
                AdvertisedGame(false, "Game 1", GameCommonId(1), "192.168.1.1", 8889),
                AdvertisedGame(false, "Game 2", GameCommonId(2), "192.168.1.2", 8889),
                AdvertisedGame(false, "Game 3", GameCommonId(3), "192.168.1.3", 8889)
            )
        )

        launchTest()

        composeTestRule.assertTextIsDisplayed(UiTags.advertisedGames)
        composeTestRule.assertTextIsDisplayedOnce("Game 1")
        composeTestRule.assertTextIsDisplayedOnce("Game 2")
        composeTestRule.assertTextIsDisplayedOnce("Game 3")
    }

    @Test
    fun advertisedGamesAreLoading() {
        advertisedGames = LoadedData.Loading

        launchTest()

        composeTestRule.assertTextIsDisplayed(UiTags.advertisedGames)
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.no_game_discovered))
    }

    @Test
    fun noAdvertisedGames() {
        advertisedGames = LoadedData.Success(emptyList())

        launchTest()

        composeTestRule.assertTextIsDisplayed(UiTags.advertisedGames)
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.no_game_discovered))
    }

    @Test
    fun errorLoadingAdvertisedGames() {
        advertisedGames = LoadedData.Failed

        launchTest()

        composeTestRule.assertTextIsDisplayed(UiTags.advertisedGames)
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.listening_advertised_games_failed))
    }

    @Test
    fun resumableGamesAreSuccesfullyDisplayed() {
        resumableGames = LoadedData.Success(
            listOf(
                ResumableGameInfo(
                    id = 1,
                    creationDate = DateTime.parse("2020-07-26T01:20+02:00"),
                    name = "LOTR",
                    playersName = listOf("Aragorn", "Legolas", "Gimli")
                ),
                ResumableGameInfo(
                    id = 2,
                    creationDate = DateTime.parse("2021-01-01T01:18+02:00"),
                    name = "GoT",
                    playersName = listOf("Ned Stark", "Arya Stark", "Sandor Clegane")
                )
            )
        )

        launchTest()

        composeTestRule.assertTextIsDisplayed(UiTags.resumableGames)
        composeTestRule.assertTextIsDisplayedOnce("LOTR", "Aragorn", "Legolas", "Gimli")
        composeTestRule.assertTextIsDisplayedOnce("GoT", "Ned Stark", "Arya Stark", "Sandor Clegane")
    }

    @Test
    fun noResumableGames() {
        resumableGames = LoadedData.Success(emptyList())

        launchTest()

        composeTestRule.onNodeWithTag(UiTags.resumableGames).assertDoesNotExist()
    }

    @Test
    fun errorLoadingResumableGames() {
        resumableGames = LoadedData.Failed

        launchTest()

        composeTestRule.assertTextIsDisplayed(UiTags.resumableGames)
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.loading_resumable_games_failed))
    }

    private fun launchTest() {
        launchTestWithContent {
            HomeBody(
                advertisedGames,
                resumableGames,
                onCreateNewGameClick = {},
                onJoinGameClick = {},
                onResumableGameClick = {}
            )
        }
    }
}
