package ch.qscqlmpa.dwitch.uitests.base

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.clickOnButton
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil.withRecyclerView
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.assertj.core.api.Assertions.assertThat
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseGuestTest : BaseOnGoingGameTest() {

    protected val gameCommonId = GameCommonId(12345)

    protected open fun goToWaitingRoom() {
        onView(withRecyclerView(R.id.gameListRw)
                .atPositionOnView(0, R.id.gameNameTv))
                .perform(ViewActions.click())

        setControlText(R.id.playerNameEdt, PlayerGuestTest.LocalGuest.name)
        setControlText(R.id.gameNameEdt, gameName)

        clickOnButton(R.id.nextBtn)

        dudeWaitASec()

        /*
        * Note: It also allows to wait for the waiting room to be displayed: otherwise, the messages sent by clients could be
        * missed because the server is not ready yet.
        */
        UiUtil.assertControlTextContent(R.id.playerListTv, R.string.wra_player_list)

        hookOngoingGameDependenciesForGuest()
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
        val gameAd = "{\"gameCommonId\":{\"value\":${gameCommonId.value}},\"gameName\":\"$gameName\",\"gamePort\":8889}"
        networkAdapter.setPacket(Packet(gameAd, hostIpAddress, 4355))
    }

    protected fun assertLocalGuestHasSentJoinGameMessage() {
        val joinGameMessage = waitForNextMessageSentByLocalGuest() as Message.JoinGameMessage
        assertThat(PlayerGuestTest.LocalGuest.name).isEqualTo(joinGameMessage.playerName)
    }

    protected fun hostSendsJoinGameAck() {
        val message = Message.JoinGameAckMessage(gameCommonId, PlayerGuestTest.LocalGuest.inGameId)
        clientTestStub.serverSendsMessageToClient(message, false)
    }

    protected fun setLocalPlayerReady() {
        clickOnButton(R.id.localPlayerReadyCkb)
        val playerReadyMessage = waitForNextMessageSentByLocalGuest() as Message.PlayerReadyMessage
        assertThat(PlayerGuestTest.LocalGuest.inGameId).isEqualTo(playerReadyMessage.playerInGameId)
        assertThat(true).isEqualTo(playerReadyMessage.ready)
    }
}