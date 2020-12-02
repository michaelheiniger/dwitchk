package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.TestUtil
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.LeaveGameMessageProcessor
import io.mockk.every
import io.mockk.verify
import io.reactivex.rxjava3.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LeaveGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var connectionStore: ConnectionStore

    private lateinit var processor: LeaveGameMessageProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    private val senderAddress = Address("192.168.1.2", 8890)

    private lateinit var senderLocalConnectionId: LocalConnectionId

    @BeforeEach
    override fun setup() {
        super.setup()
        connectionStore = ConnectionStoreFactory.createConnectionStore()

        processor = LeaveGameMessageProcessor(
                mockInGameStore,
                connectionStore,
                mockHostMessageFactory,
                TestUtil.lazyOf(mockHostCommunicator)
        )

        setupCommunicatorSendMessageCompleteMock()

        senderLocalConnectionId = connectionStore.addConnectionId(senderAddress)
        connectionStore.mapPlayerIdToConnectionId(senderLocalConnectionId, guestPlayer.inGameId)
    }

    @Test
    fun `Local connection ID of leaving player is removed from the connection store`() {

        every { mockInGameStore.deletePlayer(guestPlayer.inGameId) } returns 1
        setupWaitingRoomStateUpdateMessageMock()

        launchTest().assertComplete()

        // Assert local connection ID  of guest has been removed from store
        val guestLocalConnectionId = connectionStore.getLocalConnectionIdForAddress(senderAddress)
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