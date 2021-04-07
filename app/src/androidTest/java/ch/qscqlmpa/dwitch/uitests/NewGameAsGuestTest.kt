package ch.qscqlmpa.dwitch.uitests

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.uitests.base.BaseUiTest
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.junit.Test

class NewGameAsGuestTest : BaseUiTest() {

    private val message1 = buildSerializedAdvertisedGame(true, "Kaamelott", GameCommonId(23), 8890)
    private val message2 = buildSerializedAdvertisedGame(true, "Les Bronzés", GameCommonId(65), 8891)
    private val packet1 = Packet(message1, "192.168.1.1", 3456)
    private val packet2 = Packet(message2, "192.168.1.2", 7657)

    @Test
    fun guestMustProvideANameToJoinTheGame() {
        networkAdapter.setPacket(packet1)
        networkAdapter.setPacket(packet2)

        testRule.onNodeWithText("Les Bronzés", substring = true).performClick()

        testRule.onNodeWithTag(UiTags.playerName).performTextInput("")
        testRule.onNodeWithTag(UiTags.playerName).assertTextEquals("")
        testRule.onNodeWithText(getString(R.string.join_game)).assertIsNotEnabled()

        testRule.onNodeWithTag(UiTags.playerName).performTextInput("Mébène")
        testRule.onNodeWithText(getString(R.string.join_game)).assertIsEnabled()
    }

    @Test
    fun guestCanAbortAndComeBackToHomeScreen() {
        networkAdapter.setPacket(packet1)
        networkAdapter.setPacket(packet2)

        testRule.onNodeWithText("Les Bronzés", substring = true).performClick()

        testRule.assertTextIsDisplayedOnce(getString(R.string.back_to_home_screen))

        testRule.onNodeWithText(getString(R.string.back_to_home_screen)).performClick()
        assertCurrentScreenIsHomeSreen()
    }
}
