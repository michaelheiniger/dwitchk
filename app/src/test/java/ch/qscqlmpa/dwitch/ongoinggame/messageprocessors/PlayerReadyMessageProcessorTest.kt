package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.utils.LazyImpl
import io.mockk.every
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerReadyMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var processor: PlayerReadyMessageProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    @BeforeEach
    override fun setup() {
        super.setup()
        processor = PlayerReadyMessageProcessor(
                mockInGameStore,
                mockHostMessageFactory,
                LazyImpl(mockHostCommunicator)
        )
        setupCommunicatorSendMessageCompleteMock()
    }

    @Test
    fun `Send waiting room state update message when player ready state is updated`() {

        val newReadyState = true
        val waitingRoomStateUpdateMessageWrapperMock = setupWaitingRoomStateUpdateMessageMock()
        every { mockInGameStore.updatePlayerWithReady(guestPlayer.inGameId, newReadyState) } returns 1

        launchTest(newReadyState).test().assertComplete()

        verify { mockHostCommunicator.sendMessage(waitingRoomStateUpdateMessageWrapperMock) }
    }

    @Test
    fun `Store is updated when player ready state is updated`() {

        val newReadyState = true
        setupWaitingRoomStateUpdateMessageMock()
        every { mockInGameStore.updatePlayerWithReady(guestPlayer.inGameId, newReadyState) } returns 1

        launchTest(newReadyState).test().assertComplete()

        verify { mockInGameStore.updatePlayerWithReady(guestPlayer.inGameId, newReadyState) }
    }

    @Test
    fun `Error is thrown when player is not found in store`() {

        launchTest(true).blockingGet()

        val newReadyState = true
        setupWaitingRoomStateUpdateMessageMock()
        every { mockInGameStore.updatePlayerWithReady(guestPlayer.inGameId, newReadyState) } returns 0 // No records is updated

        launchTest(newReadyState).test().assertError(IllegalStateException::class.java)
    }

    private fun launchTest(ready: Boolean): Completable {
        return processor.process(Message.PlayerReadyMessage(guestPlayer.inGameId, ready), LocalConnectionId(0))
    }
}