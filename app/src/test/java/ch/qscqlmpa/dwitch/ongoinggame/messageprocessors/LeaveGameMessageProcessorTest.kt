package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.Address
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.utils.TestUtil
import io.mockk.every
import io.mockk.verify
import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LeaveGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var localConnectionIdStore: LocalConnectionIdStore

    private lateinit var processor: LeaveGameMessageProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    private val senderAddress = Address("192.168.1.2", 8890)

    private lateinit var senderLocalConnectionId: LocalConnectionId

    @BeforeEach
    override fun setup() {
        super.setup()
        localConnectionIdStore = LocalConnectionIdStore()

        processor = LeaveGameMessageProcessor(
                mockInGameStore,
                localConnectionIdStore,
                mockHostMessageFactory,
                TestUtil.lazyOf(mockHostCommunicator)
        )

        setupCommunicatorSendMessageCompleteMock()

        senderLocalConnectionId = localConnectionIdStore.addConnectionId(senderAddress)
        localConnectionIdStore.mapPlayerIdToConnectionId(senderLocalConnectionId, guestPlayer.inGameId)
    }

    @Test
    fun `Local connection ID of leaving player is removed from the connection store`() {

        every { mockInGameStore.deletePlayer(guestPlayer.inGameId) } returns 1
        setupWaitingRoomStateUpdateMessageMock()

        launchTest().assertComplete()

        // Assert local connection ID  of guest has been removed from store
        val guestLocalConnectionId = localConnectionIdStore.getLocalConnectionIdForAddress(senderAddress)
        assertThat(guestLocalConnectionId).isNull()
    }

    @Test
    fun `Player is deleted from store when leaving the game`() {

        every { mockInGameStore.deletePlayer(guestPlayer.inGameId) } returns 1
        setupWaitingRoomStateUpdateMessageMock()

        launchTest().assertComplete()

        verify { mockInGameStore.deletePlayer(guestPlayer.inGameId) }
    }

    @Test
    fun `Error is thrown when leaving player is not found in store`() {

        every { mockInGameStore.deletePlayer(guestPlayer.inGameId) } returns 0 // Player not found in store
        setupWaitingRoomStateUpdateMessageMock()

        launchTest().assertError(IllegalStateException::class.java)
    }

    private fun launchTest(): TestObserver<Void> {
        return processor.process(Message.LeaveGameMessage(guestPlayer.inGameId), senderLocalConnectionId).test()
    }
}
