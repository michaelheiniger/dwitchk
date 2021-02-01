package ch.qscqlmpa.dwitch.uitests.base

import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.assertj.core.api.Assertions.assertThat
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseGuestTest : BaseOnGoingGameTest() {

    protected val gameCommonId = GameCommonId(12345)

    protected open fun goToWaitingRoom() {
        advertiseGame()

        UiUtil.clickOnRecyclerViewElement(R.id.gameListRw, R.id.gameNameTv, 0)

        UiUtil.setControlText(R.id.playerNameEdt, PlayerGuestTest.LocalGuest.name)
        UiUtil.clickOnButton(R.id.nextBtn)

        dudeWaitAMillisSec()

        /*
        * Note: It also allows to wait for the waiting room to be displayed: otherwise, the messages sent by clients could be
        * missed because the server is not ready yet.
        */
        UiUtil.assertControlTextContent(R.id.playerListTv, R.string.wra_player_list)

        hookOngoingGameDependenciesForGuest()

        connectGuestToHost()

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

        dudeWaitAMillisSec()
    }

    protected fun waitForNextMessageSentByLocalGuest(): Message {
        Timber.d("Waiting for next message sent by local guest...")
        val messageSerialized = clientTestStub.observeMessagesSent()
            .take(1)
            .timeout(10, TimeUnit.SECONDS)
            .blockingFirst()
        val message = commSerializerFactory.unserializeMessage(messageSerialized)
        Timber.d("Message sent to host: $message")
        return message
    }

    protected fun advertiseGame() {
        val hostIpAddress = "192.168.1.1"
        val gameAd = buildSerializedAdvertisedGame(true, gameName, gameCommonId, 8889)
        networkAdapter.setPacket(Packet(gameAd, hostIpAddress, 4355))
    }

    private fun connectGuestToHost() {
        clientTestStub.connectClientToServer(true)
        val message = waitForNextMessageSentByLocalGuest()
        assertThat(message).isInstanceOf(Message.JoinGameMessage::class.java)
    }

    protected fun hostSendsJoinGameAck() {
        val message = Message.JoinGameAckMessage(gameCommonId, PlayerGuestTest.LocalGuest.id)
        clientTestStub.serverSendsMessageToClient(message, false)
    }

    protected fun localPlayerToggleReadyCheckbox() {
        UiUtil.clickOnButton(R.id.localPlayerReadyCkb)
        val playerReadyMessage = waitForNextMessageSentByLocalGuest()
        assertThat(playerReadyMessage).isInstanceOf(Message.PlayerReadyMessage::class.java)
    }

    private fun hostSendsInitialWaitingRoomUpdate() {
        val gameLocalIdAtHost = 1233L
        val message = Message.WaitingRoomStateUpdateMessage(
            listOf(
                Player(
                    334,
                    PlayerGuestTest.Host.id,
                    gameLocalIdAtHost,
                    PlayerGuestTest.Host.name,
                    PlayerRole.HOST,
                    PlayerConnectionState.CONNECTED,
                    true
                ),
                Player(
                    335,
                    PlayerGuestTest.LocalGuest.id,
                    gameLocalIdAtHost,
                    PlayerGuestTest.LocalGuest.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    false
                ),
                Player(
                    336,
                    PlayerGuestTest.Guest2.id,
                    gameLocalIdAtHost,
                    PlayerGuestTest.Guest2.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    true
                ),
                Player(
                    337,
                    PlayerGuestTest.Guest3.id,
                    gameLocalIdAtHost,
                    PlayerGuestTest.Guest3.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    true
                )
            )
        )
        clientTestStub.serverSendsMessageToClient(message, false)
    }
}