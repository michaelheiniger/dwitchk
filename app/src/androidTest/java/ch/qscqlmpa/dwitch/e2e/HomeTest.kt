package ch.qscqlmpa.dwitch.e2e

import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.e2e.base.BaseUiTest
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.junit.Test

class HomeTest : BaseUiTest() {

    @Test
    fun screenIsDisplayed() {
        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun advertisedGamesAreDisplayed() {
        val advertisement1 = buildSerializedAdvertisedGame(true, "Game 1", GameCommonId(23), 8890)
        val advertisement2 = buildSerializedAdvertisedGame(true, "Game 2", GameCommonId(65), 8891)

        networkAdapter.setPacket(Packet(advertisement1, "192.168.1.1", 2454))
        networkAdapter.setPacket(Packet(advertisement2, "192.168.1.2", 6543))
        testRule.assertTextIsDisplayedOnce("Game 1")
        testRule.assertTextIsDisplayedOnce("Game 2")
    }
}
