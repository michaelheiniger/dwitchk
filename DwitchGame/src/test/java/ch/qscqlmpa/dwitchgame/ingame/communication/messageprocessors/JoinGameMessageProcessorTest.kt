package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.ingame.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.TestUtil
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class JoinGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var connectionStore: ConnectionStore

    private lateinit var processor: JoinGameMessageProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    private lateinit var senderConnectionId: ConnectionId

    @BeforeEach
    fun setup() {
        connectionStore = ConnectionStoreFactory.createConnectionStore()
        processor = JoinGameMessageProcessor(
            mockInGameStore,
            TestUtil.lazyOf(mockHostCommunicator),
            mockHostMessageFactory,
            connectionStore
        )

        senderConnectionId = ConnectionId(234)
    }

    @Test
    fun `A new player joins the new game`() {
        every { mockInGameStore.gameIsNew() } returns true
        val waitingRoomStateUpdateMessageWrapperMock = setupWaitingRoomStateUpdateMessageMock()
        val joinAckMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createJoinAckMessage(any(), any()) } returns joinAckMessageWrapperMock

        every { mockInGameStore.insertNewGuestPlayer(any(), any()) } returns guestPlayer.id
        every { mockInGameStore.getPlayerDwitchId(any()) } returns guestPlayer.dwitchId

        launchTest().test().assertComplete()

        // Assert in-game ID added to store
        val connectionId = connectionStore.getConnectionId(guestPlayer.dwitchId)
        assertThat(connectionStore.getDwitchId(connectionId!!)).isEqualTo(guestPlayer.dwitchId)
        assertThat(connectionId).isEqualTo(senderConnectionId)

        verifyOrder {
            mockHostCommunicator.sendMessage(joinAckMessageWrapperMock)
            mockHostCommunicator.sendMessage(waitingRoomStateUpdateMessageWrapperMock)
        }
        confirmVerified(mockHostCommunicator)

        verify { mockInGameStore.insertNewGuestPlayer(guestPlayer.name, false) }
        verify { mockInGameStore.getPlayerDwitchId(guestPlayer.id) }
        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `A player joins the existing game`() {
        every { mockInGameStore.gameIsNew() } returns false

        launchTest().test().assertComplete()

        verify { mockHostCommunicator.closeConnectionWithClient(senderConnectionId) }
        confirmVerified(mockHostCommunicator)

        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }

    private fun launchTest(): Completable {
        return processor.process(Message.JoinGameMessage(guestPlayer.name), senderConnectionId)
    }
}
