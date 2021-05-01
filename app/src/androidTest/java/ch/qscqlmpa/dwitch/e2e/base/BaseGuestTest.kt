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
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import org.assertj.core.api.Assertions.assertThat
import org.tinylog.kotlin.Logger
import java.util.concurrent.TimeUnit

abstract class BaseGuestTest : BaseOnGoingGameTest() {

    protected val gameCommonId = GameCommonId(12345)

    protected open fun goToWaitingRoom() {
        advertiseGame()

        testRule.onNodeWithText(gameName, substring = true).performClick()
        testRule.onNodeWithTag(UiTags.playerName).performTextReplacement(PlayerGuestTest.LocalGuest.name)
        testRule.onNodeWithText(getString(R.string.join_game)).performClick()

        waitForServiceToBeStarted()

        hookOngoingGameDependenciesForGuest()

        clientTestStub.setConnectToServerOutcome(OnStartEvent.Success)

        val message = waitForNextMessageSentByLocalGuest()
        assertThat(message).isInstanceOf(Message.JoinGameMessage::class.java)

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

        // Assert that the guest is indeed in the WaitingRoom
        testRule.assertTextIsDisplayedOnce(getString(R.string.players_in_waitingroom))
    }

    protected fun waitForNextMessageSentByLocalGuest(): Message {
        Logger.debug { "Waiting for next message sent by local guest..." }
        val messageSerialized = clientTestStub.observeMessagesSent()
            .take(1)
            .timeout(5, TimeUnit.SECONDS)
            .blockingFirst()
        val message = commSerializerFactory.unserializeMessage(messageSerialized)
        Logger.debug { "Message sent to host: $message" }
        return message
    }

    protected fun advertiseGame() {
        val hostIpAddress = "192.168.1.1"
        val gameAd = buildSerializedAdvertisedGame(true, gameName, gameCommonId, 8889)
        networkAdapter.setPacket(Packet(gameAd, hostIpAddress, 4355))
    }

    protected fun hostSendsJoinGameAck() {
        val message = Message.JoinGameAckMessage(gameCommonId, PlayerGuestTest.LocalGuest.id)
        clientTestStub.serverSendsMessageToClient(message)
    }

    protected fun localPlayerToggleReadyCheckbox() {
        testRule.onNodeWithTag(UiTags.localPlayerReadyCheckbox).performClick()
        val playerReadyMessage = waitForNextMessageSentByLocalGuest()
        assertThat(playerReadyMessage).isInstanceOf(Message.PlayerReadyMessage::class.java)
    }

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
        clientTestStub.serverSendsMessageToClient(message)
    }
}
