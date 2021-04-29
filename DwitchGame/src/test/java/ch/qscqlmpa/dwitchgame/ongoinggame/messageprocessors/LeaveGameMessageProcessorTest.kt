package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.TestUtil
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.LeaveGameMessageProcessor
import io.mockk.every
import io.mockk.verify
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LeaveGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var processor: LeaveGameMessageProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    @BeforeEach
    fun setup() {
        processor = LeaveGameMessageProcessor(mockInGameStore, mockHostMessageFactory, TestUtil.lazyOf(mockHostCommunicator))
    }

    @Test
    fun `Player is deleted from store when leaving the game`() {
        every { mockInGameStore.deletePlayer(guestPlayer.dwitchId) } returns 1
        setupWaitingRoomStateUpdateMessageMock()

        launchTest().assertComplete()

        verify { mockInGameStore.deletePlayer(guestPlayer.dwitchId) }
    }

    @Test
    fun `Error is thrown when leaving player is not found in store`() {
        every { mockInGameStore.deletePlayer(guestPlayer.dwitchId) } returns 0 // Player not found in store
        setupWaitingRoomStateUpdateMessageMock()

        launchTest().assertError(IllegalStateException::class.java)
    }

    private fun launchTest(): TestObserver<Void> {
        return processor.process(Message.LeaveGameMessage(guestPlayer.dwitchId), ConnectionId(312)).test()
    }
}
