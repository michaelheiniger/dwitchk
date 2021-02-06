package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseUiTest
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.junit.Test


class NewGameAsGuestTest : BaseUiTest() {

    private val message1 = buildSerializedAdvertisedGame(true, "Kaamelott", GameCommonId(23), 8890)
    private val message2 = buildSerializedAdvertisedGame(true, "Les Bronzés", GameCommonId(65), 8891)
    private val packet1 = Packet(message1, "192.168.1.1", 3456)
    private val packet2 = Packet(message2, "192.168.1.2", 7657)

    @Test
    fun inputValidation() {
        launch()

        networkAdapter.setPacket(packet1)
        networkAdapter.setPacket(packet2)

        UiUtil.clickOnRecyclerViewElement(R.id.gameListRw, R.id.gameNameTv, 1)

        UiUtil.setControlText(R.id.playerNameEdt, "")
        UiUtil.assertControlEnabled(R.id.joinGameBtn, enabled = false)

        UiUtil.setControlText(R.id.playerNameEdt, "Bernard Morin")
        UiUtil.assertControlEnabled(R.id.joinGameBtn, enabled = true)
    }

    @Test
    fun abortJoinGame() {
        launch()

        networkAdapter.setPacket(packet1)
        networkAdapter.setPacket(packet2)

        UiUtil.clickOnRecyclerViewElement(R.id.gameListRw, R.id.gameNameTv, 1)

        UiUtil.setControlText(R.id.playerNameEdt, "Bernard Morin")

        UiUtil.clickOnButton(R.id.backBtn)
        UiUtil.assertControlTextContent(R.id.gameListTv, R.string.ma_game_list_tv)
    }
}
