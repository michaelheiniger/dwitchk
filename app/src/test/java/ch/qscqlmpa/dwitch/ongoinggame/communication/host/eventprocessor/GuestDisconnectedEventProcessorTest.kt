package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessor

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.GuestDisconnectedEventProcessor
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.Address
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.utils.LazyImpl
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GuestDisconnectedEventProcessorTest : BaseUnitTest() {

    private lateinit var localConnectionIdStore: LocalConnectionIdStore

    private val mockHostMessageFactory = mockk<HostMessageFactory>(relaxed = true)

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val senderAddress = Address("192.168.1.2", 8890)

    private lateinit var processor: GuestDisconnectedEventProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1().copy(connectionState = PlayerConnectionState.CONNECTED)

    @BeforeEach
    override fun setup() {
        super.setup()
        localConnectionIdStore = LocalConnectionIdStore()
        processor = GuestDisconnectedEventProcessor(
                mockInGameStore,
                localConnectionIdStore,
                mockHostMessageFactory,
                LazyImpl(mockCommunicator)
        )

        every { mockCommunicator.sendMessage(any()) } returns Completable.complete()
    }

    @Test
    fun `Client disconnects`() {

        every { mockInGameStore.updatePlayer(any(), any(), any()) } returns 1 // Record is found and updated

        val guestLocalConnectionId = setupLocalConnectionIdStore()

        val waitingRoomStateUpdateMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createWaitingRoomStateUpdateMessage() } returns Single.just(waitingRoomStateUpdateMessageWrapperMock)

        launchTest(guestLocalConnectionId).test().assertComplete()

        verify { mockCommunicator.sendMessage(waitingRoomStateUpdateMessageWrapperMock) }
        verify { mockInGameStore.updatePlayer(guestPlayer.inGameId, PlayerConnectionState.DISCONNECTED, false) }
        assertThat(localConnectionIdStore.getInGameId(guestLocalConnectionId)).isNull()
    }

    @Test
    fun `Nothing to do when no player in-game ID for given connection identifier`() {

        // ConnectionIdentifierStore is empty

        launchTest(LocalConnectionId(0)).test().assertComplete()

        verify { mockCommunicator wasNot Called }
        verify { mockHostMessageFactory wasNot Called }
        verify { mockInGameStore wasNot Called }
        assertThat(localConnectionIdStore.getInGameId(LocalConnectionId(0))).isNull()
        confirmVerified(mockCommunicator)
        confirmVerified(mockHostMessageFactory)
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Nothing to do when player does not exist in store`() {

        // No corresponding guest exists in store. This should never happen.
        every { mockInGameStore.updatePlayer(any(), any(), any()) } returns 0

        val guestLocalConnectionId = setupLocalConnectionIdStore()

        launchTest(guestLocalConnectionId).test().assertError(IllegalStateException::class.java)

        assertThat(localConnectionIdStore.getInGameId(guestLocalConnectionId)).isNull()
    }

    private fun launchTest(guestLocalConnectionId: LocalConnectionId): Completable {
        return processor.process(ServerCommunicationEvent.ClientDisconnected(guestLocalConnectionId))
    }

    private fun setupLocalConnectionIdStore(): LocalConnectionId {
        val guestLocalConnectionId = localConnectionIdStore.addConnectionId(senderAddress)
        localConnectionIdStore.mapPlayerIdToConnectionId(guestLocalConnectionId, guestPlayer.inGameId)
        return guestLocalConnectionId
    }
}