package ch.qscqlmpa.dwitch.e2e.base

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test.OnStartEvent
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import org.tinylog.kotlin.Logger
import java.util.*

abstract class BaseGuestTest : BaseOnGoingGameTest() {

    protected val gameCommonId = GameCommonId(UUID.randomUUID())

    protected fun goToWaitingRoomWithHostAndLocalGuest(localGuestConnected: Boolean = true) {
        goToWaitingRoom(
            PlayerGuestTest.Host.info,
            PlayerGuestTest.LocalGuest.info.copy(connected = localGuestConnected, ready = false)
        )
    }

    protected fun goToWaitingRoomWithHostAndAllGuests(
        localGuestConnected: Boolean = true,
        guest2Connected: Boolean = true,
        guest3Connected: Boolean = true
    ) {
        goToWaitingRoom(
            PlayerGuestTest.Host.info,
            PlayerGuestTest.LocalGuest.info.copy(connected = localGuestConnected, ready = false),
            PlayerGuestTest.Guest2.info.copy(connected = guest2Connected),
            PlayerGuestTest.Guest3.info.copy(connected = guest3Connected)
        )
    }

    private fun goToWaitingRoom(vararg players: PlayerWr) {
        advertiseGameToJoin()

        testRule.onNodeWithText(gameName, substring = true).performClick()
        testRule.onNodeWithTag(UiTags.playerName).performTextReplacement(PlayerGuestTest.LocalGuest.info.name)
        testRule.onNodeWithText(getString(R.string.join_game)).performClick()

        testRule.waitForIdle() // Can't hook in-game dependencies before components are created
        hookOngoingGameDependenciesForGuest()

        connectClientToServer(OnStartEvent.Success)
        waitForNextMessageSentByLocalGuest() as Message.JoinGameMessage

        hostSendsJoinGameAck()
        hostSendsPlayersState(*players)

        // Assert that the guest is indeed in the WaitingRoom
        testRule.assertTextIsDisplayedOnce(getString(R.string.players_in_waitingroom))
    }

    protected fun connectClientToServer(onStartEvent: OnStartEvent, incrementIdlingResource: Boolean = false) {
        // Must not increment when joining game from home screen because we wait for ack from host before joining WR
        // Required when reconnecting since we are already in either WR or GR
        if (incrementIdlingResource) incrementGameIdlingResource("Client connects to server (linked with comm state)")
        clientTestStub.connectClientToServer(onStartEvent)
    }

    protected fun advertiseGameToJoin() {
        advertiseGame(
            isNew = true,
            gameName = gameName,
            gameCommonId = gameCommonId,
            gamePort = 8890,
            gameIpAddress = "192.168.1.1",
            senderPort = 2454
        )
    }

    protected fun waitForNextMessageSentByLocalGuest(): Message {
        Logger.debug { "Waiting for next message sent by local guest..." }
        val messageSerialized = clientTestStub.blockUntilMessageSentIsAvailable()
        return commSerializerFactory.unserializeMessage(messageSerialized)
    }

    protected fun hostSendsJoinGameAck() {
        val message = Message.JoinGameAckMessage(gameCommonId, PlayerGuestTest.LocalGuest.info.dwitchId)
        clientTestStub.serverSendsMessageToClient(message)
    }

    protected fun hostSendsRejoinGameAck_waitingRoom() {
        val message = Message.RejoinGameAckMessage(gameCommonId, RoomType.WAITING_ROOM, PlayerGuestTest.LocalGuest.info.dwitchId)
        clientTestStub.serverSendsMessageToClient(message)
    }

    protected fun localPlayerToggleReadyCheckbox() {
        testRule.onNodeWithTag(UiTags.localPlayerReadyControl).performClick()
        waitForNextMessageSentByLocalGuest() as Message.PlayerReadyMessage
    }

    private fun hostSendsPlayersState(vararg players: PlayerWr) {
        val message = Message.WaitingRoomStateUpdateMessage(listOf(*players))
        clientTestStub.serverSendsMessageToClient(message)
    }
}
