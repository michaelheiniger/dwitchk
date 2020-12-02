package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
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

class GuestDisconnectedEventProcessorTest : BaseUnitTest() {

    private lateinit var connectionStore: ConnectionStore

    private val mockHostMessageFactory = mockk<HostMessageFactory>(relaxed = true)

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val senderAddress = Address("192.168.1.2", 8890)

    private lateinit var processor: GuestDisconnectedEventProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1().copy(connectionState = PlayerConnectionState.CONNECTED)

    @BeforeEach
    override fun setup() {
        super.setup()
        connectionStore = ConnectionStoreFactory.createConnectionStore()
        processor = GuestDisconnectedEventProcessor(
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

        val guestLocalConnectionId = setupConnectionStore()

        val waitingRoomStateUpdateMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createWaitingRoomStateUpdateMessage() } returns Single.just(waitingRoomStateUpdateMessageWrapperMock)

        launchTest(guestLocalConnectionId).test().assertComplete()

        verify { mockCommunicator.sendMessage(waitingRoomStateUpdateMessageWrapperMock) }
        verify { mockInGameStore.updatePlayer(guestPlayer.inGameId, PlayerConnectionState.DISCONNECTED, false) }
        assertThat(connectionStore.getInGameId(guestLocalConnectionId)).isNull()
    }

    @Test
    fun `Nothing to do when no player in-game ID for given connection identifier`() {

        // ConnectionIdentifierStore is empty

        launchTest(LocalConnectionId(0)).test().assertComplete()

        verify { mockCommunicator wasNot Called }
        verify { mockHostMessageFactory wasNot Called }
        verify { mockInGameStore wasNot Called }
        assertThat(connectionStore.getInGameId(LocalConnectionId(0))).isNull()
        confirmVerified(mockCommunicator)
        confirmVerified(mockHostMessageFactory)
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Nothing to do when player does not exist in store`() {

        // No corresponding guest exists in store. This should never happen.
        every { mockInGameStore.updatePlayer(any(), any(), any()) } returns 0

        val guestLocalConnectionId = setupConnectionStore()

        launchTest(guestLocalConnectionId).test().assertError(IllegalStateException::class.java)

        assertThat(connectionStore.getInGameId(guestLocalConnectionId)).isNull()
    }

    private fun launchTest(guestLocalConnectionId: LocalConnectionId): Completable {
        return processor.process(ServerCommunicationEvent.ClientDisconnected(guestLocalConnectionId))
    }

    private fun setupConnectionStore(): LocalConnectionId {
        val guestLocalConnectionId = connectionStore.addConnectionId(senderAddress)
        connectionStore.mapPlayerIdToConnectionId(guestLocalConnectionId, guestPlayer.inGameId)
        return guestLocalConnectionId
    }
}