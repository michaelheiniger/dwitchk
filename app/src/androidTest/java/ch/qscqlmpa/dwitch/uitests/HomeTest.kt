package ch.qscqlmpa.dwitch.uitests

import ch.qscqlmpa.dwitch.uitests.base.BaseUiTest
import org.junit.Ignore

@Ignore
class HomeTest : BaseUiTest() {

//    @Test
//    fun screenIsDisplayed() {
//        launch()
//
//        assertCurrentScreenIsHomeSreen()
//    }
//
//    @Test
//    fun advertisedGameListIsSet() {
//        launch()
//
//            Iding
//        val message1 = buildSerializedAdvertisedGame(true, "Game 1", GameCommonId(23), 8890)
//        val message2 = buildSerializedAdvertisedGame(true, "Game 2", GameCommonId(65), 8891)
//
//        networkAdapter.setPacket(Packet(message1, "192.168.1.1", 2454))
////        networkAdapter.setPacket(Packet(message2, "192.168.1.2", 6543))
//
//
////        val networkAdapterIdlingResource = object: IdlingResource {
////            override val isIdleNow: Boolean
////                get() {
////
////                }
////        }
//
////        testRule.registerIdlingResource(idlingResource)
//
//
//        testRule.waitUntil(timeoutMillis = 5000) {
//            testRule.onNodeWithText("Game 1 (192.168.1.1)").
////            app.testGameComponent.guestFacade.listenForAdvertisedGames().blockingFirst().size == 1
//        }
//
//        testRule.onNodeWithText("Game 1 (192.168.1.1)").assertExists()
//
////        assertGameInGameList(0, "Game 1 (192.168.1.1)")
////        assertGameInGameList(1, "Game 2 (192.168.1.2)")
//    }
////
////    private fun assertGameInGameList(position: Int, beginningOfName: String) {
////        UiUtil.assertRecyclerViewElementText(R.id.gameListRw, R.id.gameNameTv, position, startsWith(beginningOfName))
////    }
}
