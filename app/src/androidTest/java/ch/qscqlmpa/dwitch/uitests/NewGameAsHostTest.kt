package ch.qscqlmpa.dwitch.uitests

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseUiTest
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import org.junit.Test


class NewGameAsHostTest : BaseUiTest() {

    @Test
    fun createGame() {
        launch()

        val gameName = "Les Bronzés font du ski"
        val playerName = "Jean-Claude Duss"

        UiUtil.clickOnButton(R.id.createGameBtn)

        UiUtil.setControlText(R.id.playerNameEdt, playerName)
        UiUtil.setControlText(R.id.gameNameEdt, gameName)

        UiUtil.assertControlTextContent(R.id.playerNameEdt, playerName)
        UiUtil.assertControlTextContent(R.id.gameNameEdt, gameName)
        UiUtil.assertControlEnabled(R.id.gameNameEdt, true)
    }

    @Test
    fun createGame_inputValidation() {
        launch()

        UiUtil.clickOnButton(R.id.createGameBtn)

        // Player name
        UiUtil.setControlText(R.id.playerNameEdt, "")
        UiUtil.setControlText(R.id.gameNameEdt, "Les Bronzés font du ski")
        UiUtil.clickOnButton(R.id.nextBtn)
        UiUtil.assertControlErrorTextContent(R.id.playerNameEdt, R.string.nge_player_name_empty)

        // Game name
        UiUtil.setControlText(R.id.playerNameEdt, "Jean-Claude Duss")
        UiUtil.setControlText(R.id.gameNameEdt, "")
        UiUtil.clickOnButton(R.id.nextBtn)
        UiUtil.assertControlErrorTextContent(R.id.gameNameEdt, R.string.nge_game_name_empty)
    }

    @Test
    fun abortCreateGame() {
        launch()

        UiUtil.clickOnButton(R.id.createGameBtn)
        UiUtil.assertTextInputLayoutHint(R.id.playerNameTil, R.string.nga_player_name_tv)
        UiUtil.assertTextInputLayoutHint(R.id.gameNameTil, R.string.nga_game_name_tv)

        UiUtil.clickOnButton(R.id.backBtn)
        UiUtil.assertControlTextContent(R.id.gameListTv, R.string.ma_game_list_tv)
    }
}
