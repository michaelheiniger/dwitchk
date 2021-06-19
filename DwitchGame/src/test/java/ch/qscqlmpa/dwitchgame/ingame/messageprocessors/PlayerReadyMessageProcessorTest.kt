package ch.qscqlmpa.dwitchgame.ingame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.LazyImpl
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.PlayerReadyMessageProcessor
import io.mockk.every
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PlayerReadyMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var processor: PlayerReadyMessageProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    @BeforeEach
    fun setup() {
        processor = PlayerReadyMessageProcessor(
            mockInGameStore,
            mockHostMessageFactory,
            LazyImpl(mockHostCommunicator)
        )
    }

    @Test
    fun `Send waiting room state update message when player ready state is updated`() {

        val newReadyState = true
        val waitingRoomStateUpdateMessageWrapperMock = setupWaitingRoomStateUpdateMessageMock()
        every { mockInGameStore.updatePlayerWithReady(guestPlayer.dwitchId, newReadyState) } returns 1

        launchTest(newReadyState).test().assertComplete()

        verify { mockHostCommunicator.sendMessage(waitingRoomStateUpdateMessageWrapperMock) }
    }

    @Test
    fun `Store is updated when player ready state is updated`() {

        val newReadyState = true
        setupWaitingRoomStateUpdateMessageMock()
        every { mockInGameStore.updatePlayerWithReady(guestPlayer.dwitchId, newReadyState) } returns 1

        launchTest(newReadyState).test().assertComplete()

        verify { mockInGameStore.updatePlayerWithReady(guestPlayer.dwitchId, newReadyState) }
    }

    @Test
    fun `Error is thrown when player is not found in store`() {

        launchTest(true).blockingSubscribe()

        val newReadyState = true
        setupWaitingRoomStateUpdateMessageMock()
        every { mockInGameStore.updatePlayerWithReady(guestPlayer.dwitchId, newReadyState) } returns 0 // No records is updated

        launchTest(newReadyState).test().assertError(IllegalStateException::class.java)
    }

    private fun launchTest(ready: Boolean): Completable {
        return processor.process(Message.PlayerReadyMessage(guestPlayer.dwitchId, ready), ConnectionId(0))
    }
}
