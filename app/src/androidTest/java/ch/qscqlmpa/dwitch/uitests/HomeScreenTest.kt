package ch.qscqlmpa.dwitch.uitests

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseUiTest
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.hamcrest.Matchers.startsWith
import org.junit.Test

class HomeScreenTest : BaseUiTest() {

    @Test
    fun screenIsDisplayed() {
        launch()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun advertisedGameListIsSet() {
        launch()

        val message1 = buildSerializedAdvertisedGame(true, "Game 1", GameCommonId(23), 8890)
        val message2 = buildSerializedAdvertisedGame(true, "Game 2", GameCommonId(65), 8891)

        networkAdapter.setPacket(Packet(message1, "192.168.1.1", 2454))
        networkAdapter.setPacket(Packet(message2, "192.168.1.2", 6543))

        assertGameInGameList(0, "Game 1 (192.168.1.1)")
        assertGameInGameList(1, "Game 2 (192.168.1.2)")
    }

    private fun assertGameInGameList(position: Int, beginningOfName: String) {
        UiUtil.assertRecyclerViewElementText(R.id.gameListRw, R.id.gameNameTv, position, startsWith(beginningOfName))
    }
}
