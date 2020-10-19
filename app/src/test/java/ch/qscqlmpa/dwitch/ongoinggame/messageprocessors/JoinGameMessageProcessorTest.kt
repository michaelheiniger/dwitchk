package ch.qscqlmpa.dwitch.components.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.communication.Address
import ch.qscqlmpa.dwitch.components.game.TestEntityFactory
import ch.qscqlmpa.dwitch.components.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.components.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.components.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.components.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.utils.TestUtil
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JoinGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var localConnectionIdStore: LocalConnectionIdStore

    private lateinit var processor: JoinGameMessageProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    private val senderAddress = Address("192.168.1.2", 8890)

    private lateinit var senderLocalConnectionId: LocalConnectionId

    @BeforeEach
    override fun setup() {
        super.setup()
        localConnectionIdStore = LocalConnectionIdStore()
        processor = JoinGameMessageProcessor(
                mockInGameStore,
                TestUtil.lazyOf(mockHostCommunicator),
                mockHostMessageFactory,
                localConnectionIdStore
        )

        setupCommunicatorSendMessageCompleteMock()
        senderLocalConnectionId = localConnectionIdStore.addAddress(senderAddress)
        localConnectionIdStore.addPlayerInGameId(senderLocalConnectionId, guestPlayer.inGameId)

    }

    @Test
    fun `A new player record is inserted in store when new player joins the game`() {

        setupWaitingRoomStateUpdateMessageMock()
        val joinAckMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createJoinAckMessage(any(), any()) } returns Single.just(joinAckMessageWrapperMock)

        every { mockInGameStore.insertNewGuestPlayer(any()) } returns guestPlayer.id
        every { mockInGameStore.getPlayerInGameId(any()) } returns guestPlayer.inGameId

        launchTest().test().assertComplete()

        verify { mockInGameStore.insertNewGuestPlayer(guestPlayer.name) }
        verify { mockInGameStore.getPlayerInGameId(guestPlayer.id) }

        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Local connection ID of the joining player is stored in connection store`() {

        setupWaitingRoomStateUpdateMessageMock()
        val joinAckMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createJoinAckMessage(any(), any()) } returns Single.just(joinAckMessageWrapperMock)

        every { mockInGameStore.insertNewGuestPlayer(any()) } returns guestPlayer.id
        every { mockInGameStore.getPlayerInGameId(any()) } returns guestPlayer.inGameId

        launchTest().test().assertComplete()

        // Assert in-game ID added to store
        val localConnectionId = localConnectionIdStore.getLocalConnectionIdForAddress(senderAddress)
        assertThat(localConnectionIdStore.getInGameId(localConnectionId!!)).isEqualTo(guestPlayer.inGameId)
    }

    @Test
    fun `A "join ack" and "waiting room state update" messages are sent when a new player joins the game`() {

        val waitingRoomStateUpdateMessageWrapperMock = setupWaitingRoomStateUpdateMessageMock()
        val joinAckMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createJoinAckMessage(any(), any()) } returns Single.just(joinAckMessageWrapperMock)

        every { mockInGameStore.insertNewGuestPlayer(any()) } returns guestPlayer.id
        every { mockInGameStore.getPlayerInGameId(any()) } returns guestPlayer.inGameId

        launchTest().test().assertComplete()

        verifyOrder {
            mockHostCommunicator.sendMessage(joinAckMessageWrapperMock)
            mockHostCommunicator.sendMessage(waitingRoomStateUpdateMessageWrapperMock)
        }

        confirmVerified(mockHostCommunicator)
    }

    private fun launchTest(): Completable {
        return processor.process(Message.JoinGameMessage(guestPlayer.name), senderLocalConnectionId)
    }
}