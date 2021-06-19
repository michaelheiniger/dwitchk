package ch.qscqlmpa.dwitch.e2e.base

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.OnStartEvent
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import org.assertj.core.api.Assertions.assertThat
import org.tinylog.kotlin.Logger

abstract class BaseGuestTest : BaseOnGoingGameTest() {

    protected val gameCommonId = GameCommonId(12345)

    protected open fun goToWaitingRoom() {
        advertiseGameToJoin()

        testRule.onNodeWithText(gameName, substring = true).performClick()
        testRule.onNodeWithTag(UiTags.playerName).performTextReplacement(PlayerGuestTest.LocalGuest.name)
        testRule.onNodeWithText(getString(R.string.join_game)).performClick()

        testRule.waitForIdle() // Can't hook on-going game dependencies before component is created
        hookOngoingGameDependenciesForGuest()
        connectClientToServer(OnStartEvent.Success)

        val message = waitForNextMessageSentByLocalGuest()
        assertThat(message).isInstanceOf(Message.JoinGameMessage::class.java)

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

        // Assert that the guest is indeed in the WaitingRoom
        testRule.assertTextIsDisplayedOnce(getString(R.string.players_in_waitingroom))
    }

    protected fun connectClientToServer(onStartEvent: OnStartEvent) {
        incrementGameIdlingResource("Client connects to server (linked with comm state)")
        clientTestStub.connectClientToServer(onStartEvent)
    }

    protected fun advertiseGameToJoin() {
        advertiseGame(
            isNew = true,
            gameName = gameName,
            gameCommonId = gameCommonId,
            gamePort = 8890,
            senderIpAddress = "192.168.1.1",
            senderPort = 2454
        )
    }

    protected fun waitForNextMessageSentByLocalGuest(): Message {
        Logger.debug { "Waiting for next message sent by local guest..." }
        val messageSerialized = clientTestStub.blockUntilMessageSentIsAvailable()
        return commSerializerFactory.unserializeMessage(messageSerialized)
    }

    protected fun hostSendsJoinGameAck() {
        val message = Message.JoinGameAckMessage(gameCommonId, PlayerGuestTest.LocalGuest.id)
        clientTestStub.serverSendsMessageToClient(message)
    }

    protected fun localPlayerToggleReadyCheckbox() {
        testRule.onNodeWithTag(UiTags.localPlayerReadyControl).performClick()
        val playerReadyMessage = waitForNextMessageSentByLocalGuest()
        assertThat(playerReadyMessage).isInstanceOf(Message.PlayerReadyMessage::class.java)
    }

    private fun hostSendsInitialWaitingRoomUpdate() {
        incrementGameIdlingResource("Host sends initial state of WR players")
        val message = Message.WaitingRoomStateUpdateMessage(
            listOf(
                PlayerWr(
                    PlayerGuestTest.Host.id,
                    PlayerGuestTest.Host.name,
                    PlayerRole.HOST,
                    connected = true,
                    ready = true
                ),
                PlayerWr(
                    PlayerGuestTest.LocalGuest.id,
                    PlayerGuestTest.LocalGuest.name,
                    PlayerRole.GUEST,
                    connected = true,
                    ready = false
                ),
                PlayerWr(
                    PlayerGuestTest.Guest2.id,
                    PlayerGuestTest.Guest2.name,
                    PlayerRole.GUEST,
                    connected = true,
                    ready = true
                ),
                PlayerWr(
                    PlayerGuestTest.Guest3.id,
                    PlayerGuestTest.Guest3.name,
                    PlayerRole.GUEST,
                    connected = true,
                    ready = true
                )
            )
        )
        clientTestStub.serverSendsMessageToClient(message)
    }
}
