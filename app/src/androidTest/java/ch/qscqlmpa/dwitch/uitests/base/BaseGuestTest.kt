package ch.qscqlmpa.dwitch.uitests.base

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr

abstract class BaseGuestTest : BaseOnGoingGameTest() {

    protected val gameCommonId = GameCommonId(12345)

    protected open fun goToWaitingRoom() {
        advertiseGame()

        testRule.onNodeWithText(gameName, substring = true).performClick()
        testRule.onNodeWithTag("playerName").performTextInput(PlayerGuestTest.LocalGuest.name)
        testRule.onNodeWithText(getString(R.string.join_game)).performClick()

//        dudeWaitAMillisSec()

        /*
        * Note: It also allows to wait for the waiting room to be displayed: otherwise, the messages sent by clients could be
        * missed because the server is not ready yet.
        */
//        UiUtil.assertControlTextContent(R.id.playerListTv, R.string.wra_player_list)

        hookOngoingGameDependenciesForGuest()

        connectGuestToHost()

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

//        dudeWaitAMillisSec()
        // Assert that the guest is indeed in the WaitingRoom
        testRule.assertTextIsDisplayedOnce(getString(R.string.players_in_waitingroom))
        testRule.assertTextIsDisplayedOnce(getString(R.string.leave_game_btn))

    }

    //    protected fun waitForNextMessageSentByLocalGuest(): Message {
//        Logger.debug { "Waiting for next message sent by local guest..." }
//        val messageSerialized = clientTestStub.observeMessagesSent()
//            .take(1)
//            .timeout(10, TimeUnit.SECONDS)
//            .blockingFirst()
//        val message = commSerializerFactory.unserializeMessage(messageSerialized)
//        Logger.debug { "Message sent to host: $message" }
//        return message
//    }
//
    protected fun advertiseGame() {
        val hostIpAddress = "192.168.1.1"
        val gameAd = buildSerializedAdvertisedGame(true, gameName, gameCommonId, 8889)
        networkAdapter.setPacket(Packet(gameAd, hostIpAddress, 4355))
    }

    private fun connectGuestToHost() {
        clientTestStub.connectClientToServer(true)
//        val message = waitForNextMessageSentByLocalGuest()
//        assertThat(message).isInstanceOf(Message.JoinGameMessage::class.java)
    }

    protected fun hostSendsJoinGameAck() {
        val message = Message.JoinGameAckMessage(gameCommonId, PlayerGuestTest.LocalGuest.id)
        clientTestStub.serverSendsMessageToClient(message, false)
    }

    //    protected fun localPlayerToggleReadyCheckbox() {
//        UiUtil.clickOnButton(R.id.localPlayerReadyCkb)
//        val playerReadyMessage = waitForNextMessageSentByLocalGuest()
//        assertThat(playerReadyMessage).isInstanceOf(Message.PlayerReadyMessage::class.java)
//    }
//
    private fun hostSendsInitialWaitingRoomUpdate() {
        val message = Message.WaitingRoomStateUpdateMessage(
            listOf(
                PlayerWr(
                    PlayerGuestTest.Host.id,
                    PlayerGuestTest.Host.name,
                    PlayerRole.HOST,
                    PlayerConnectionState.CONNECTED,
                    ready = true
                ),
                PlayerWr(
                    PlayerGuestTest.LocalGuest.id,
                    PlayerGuestTest.LocalGuest.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    ready = false
                ),
                PlayerWr(
                    PlayerGuestTest.Guest2.id,
                    PlayerGuestTest.Guest2.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    ready = true
                ),
                PlayerWr(
                    PlayerGuestTest.Guest3.id,
                    PlayerGuestTest.Guest3.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    ready = true
                )
            )
        )
        clientTestStub.serverSendsMessageToClient(message, false)
    }
}
