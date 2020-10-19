package ch.qscqlmpa.dwitch.acceptancetests

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.communication.client.LocalGuest
import ch.qscqlmpa.dwitch.components.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.gamediscovery.network.Packet
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil
import org.junit.Assert
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseGuestTest : BaseOnGoingGameTest() {

    protected open fun goToWaitingRoom() {
        Espresso.onView(ViewAssertionUtil.withRecyclerView(R.id.gameListRw)
                .atPositionOnView(0, R.id.gameNameTv))
                .perform(ViewActions.click())

        setControlText(R.id.playerNameEdt, LocalGuest.name)
        setControlText(R.id.gameNameEdt, gameName)

        clickOnButton(R.id.nextBtn)

        dudeWaitAMinute(1)

        /*
        * Note: It also allows to wait for the waiting room to be displayed: otherwise, the messages sent by clients could be
        * missed because the server is not ready yet.
        */
        assertControlTextContent(R.id.playerListTv, R.string.wra_player_list)

        hookOngoingGameDependenciesForGuest()
    }

    protected fun waitForNextMessageSentByLocalGuest(): Message {
        Timber.d("Waiting for next message sent by local guest...")
        val messageSerialized = clientTestStub.observeMessagesSent()
                .take(1)
                .timeout(10, TimeUnit.SECONDS)
                .blockingFirst()
        val message = serializerFactory.unserializeMessage(messageSerialized)
        Timber.d("Message sent to host: %s", message)
        return message
    }

    protected fun advertiseGame() {
        val hostIpAddress = "192.168.1.1"
        val hostPort = 8889
        networkAdapter.setPacket(Packet(gameName, hostIpAddress, hostPort))
    }

    protected fun setLocalPlayerReady() {
        Espresso.onView(ViewMatchers.withId(R.id.localPlayerReadyCkb)).perform(ViewActions.click())
        val playerReadyMessage = waitForNextMessageSentByLocalGuest() as Message.PlayerReadyMessage
        Assert.assertEquals(LocalGuest.inGameId, playerReadyMessage.playerInGameId)
        Assert.assertEquals(true, playerReadyMessage.ready)
    }
}