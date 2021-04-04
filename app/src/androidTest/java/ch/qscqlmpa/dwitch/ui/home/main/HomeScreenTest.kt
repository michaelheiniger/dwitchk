package ch.qscqlmpa.dwitch.ui.home.main

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitch.ui.common.LoadedData
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

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.advertised_games))
        composeTestRule.assertTextIsDisplayedOnce("Game 1")
        composeTestRule.assertTextIsDisplayedOnce("Game 2")
        composeTestRule.assertTextIsDisplayedOnce("Game 3")
    }

    @Test
    fun advertisedGamesAreLoading() {
        advertisedGames = LoadedData.Loading

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.advertised_games))
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.listening_for_advertised_games))
    }

    @Test
    fun noAdvertisedGames() {
        advertisedGames = LoadedData.Success(emptyList())

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.advertised_games))
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.listening_for_advertised_games))
    }

    @Test
    fun errorLoadingAdvertisedGames() {
        advertisedGames = LoadedData.Failed

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.advertised_games))
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.listening_advertised_games_failed))
    }

    @Test
    fun resumableGamesAreSuccesfullyDisplayed() {
        resumableGames = LoadedData.Success(
            listOf(
                ResumableGameInfo(1, DateTime.parse("2020-07-26T01:20+02:00"), "LOTR", listOf("Aragorn", "Legolas", "Gimli")),
                ResumableGameInfo(
                    2,
                    DateTime.parse("2021-01-01T01:18+02:00"),
                    "GoT",
                    listOf("Ned Stark", "Arya Stark", "Sandor Clegane")
                )
            )
        )

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce("Resumable games")
        composeTestRule.assertTextIsDisplayedOnce("LOTR", "Aragorn", "Legolas", "Gimli")
        composeTestRule.assertTextIsDisplayedOnce("GoT", "Ned Stark", "Arya Stark", "Sandor Clegane")
    }

    @Test
    fun resumableGamesAreLoading() {
        resumableGames = LoadedData.Loading

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.resumable_games))
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.loading_resumable_games))
    }

    @Test
    fun noResumableGames() {
        resumableGames = LoadedData.Success(emptyList())

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.resumable_games))
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.no_resumable_games))
    }

    @Test
    fun errorLoadingResumableGames() {
        resumableGames = LoadedData.Failed

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.resumable_games))
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.loading_resumable_games_failed))
    }

    private fun launchTest() {
        launchTestWithContent {
            HomeScreen(
                advertisedGames,
                resumableGames,
                onCreateNewGameClick = {},
                onJoinGameClick = {},
                onResumableGameClick = {}
            )
        }
    }
}