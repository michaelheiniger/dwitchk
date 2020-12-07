package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.LazyImpl
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClientDisconnectedEventProcessorTest : BaseUnitTest() {

    private lateinit var connectionStore: ConnectionStore

    private val mockHostMessageFactory = mockk<HostMessageFactory>(relaxed = true)

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private lateinit var processor: ClientDisconnectedEventProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1().copy(connectionState = PlayerConnectionState.CONNECTED)

    @BeforeEach
    override fun setup() {
        super.setup()
        connectionStore = ConnectionStoreFactory.createConnectionStore()
        processor = ClientDisconnectedEventProcessor(
                mockInGameStore,
                connectionStore,
                mockHostMessageFactory,
                LazyImpl(mockCommunicator)
        )

        every { mockCommunicator.sendMessage(any()) } returns Completable.complete()
    }

    @Test
    fun `Client disconnects`() {

        every { mockInGameStore.updatePlayer(any(), any(), any()) } returns 1 // Record is found and updated

        val guestConnectionId = setupConnectionStore()

        val waitingRoomStateUpdateMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createWaitingRoomStateUpdateMessage() } returns Single.just(waitingRoomStateUpdateMessageWrapperMock)

        launchTest(guestConnectionId).test().assertComplete()

        verify { mockCommunicator.sendMessage(waitingRoomStateUpdateMessageWrapperMock) }
        verify { mockInGameStore.updatePlayer(guestPlayer.inGameId, PlayerConnectionState.DISCONNECTED, false) }
        assertThat(connectionStore.getInGameId(guestConnectionId)).isNull()
    }

    @Test
    fun `Nothing to do when no player in-game ID for given connection identifier`() {

        // ConnectionIdStore is empty

        launchTest(ConnectionId(0)).test().assertComplete()

        verify { mockCommunicator wasNot Called }
        verify { mockHostMessageFactory wasNot Called }
        verify { mockInGameStore wasNot Called }
        assertThat(connectionStore.getInGameId(ConnectionId(0))).isNull()
        confirmVerified(mockCommunicator)
        confirmVerified(mockHostMessageFactory)
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Nothing to do when player does not exist in store`() {

        // No corresponding guest exists in store. This should never happen.
        every { mockInGameStore.updatePlayer(any(), any(), any()) } returns 0

        val guestConnectionId = setupConnectionStore()

        launchTest(guestConnectionId).test().assertError(IllegalStateException::class.java)

        assertThat(connectionStore.getInGameId(guestConnectionId)).isNull()
    }

    private fun launchTest(guestConnectionId: ConnectionId): Completable {
        return processor.process(ServerCommunicationEvent.ClientDisconnected(guestConnectionId))
    }

    private fun setupConnectionStore(): ConnectionId {
        val guestConnectionId = ConnectionId(545)
        connectionStore.pairConnectionWithPlayer(guestConnectionId, guestPlayer.inGameId)
        return guestConnectionId
    }
}