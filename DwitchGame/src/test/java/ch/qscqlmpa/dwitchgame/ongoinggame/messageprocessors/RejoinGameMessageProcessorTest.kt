package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RejoinInfo
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.TestUtil
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.RejoinGameMessageProcessor
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RejoinGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var connectionStore: ConnectionStore

    private lateinit var processor: RejoinGameMessageProcessor

    private val game = TestEntityFactory.createGameInWaitingRoom()

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    private val senderLocalConnectionId = ConnectionId(0)

    private lateinit var mockWwaitingRoomStateUpdateMessageWrapper: EnvelopeToSend

    @BeforeEach
    override fun setup() {
        super.setup()
        connectionStore = ConnectionStoreFactory.createConnectionStore()

        processor = RejoinGameMessageProcessor(
            mockInGameStore,
            TestUtil.lazyOf(mockHostCommunicator),
            mockHostMessageFactory,
            connectionStore
        )
        mockGame()
        mockGuestPlayerFound()
        mockUpdatePlayerWthConnectionStateAndReady()
        setupCommunicatorSendMessageCompleteMock()
        mockWwaitingRoomStateUpdateMessageWrapper = setupWaitingRoomStateUpdateMessageMock()
    }

    @Test
    fun `Send rejoin game ACK and waiting room state updatewhen rejoining player is found in store`() {
        launchTest()

        verify {
            mockHostCommunicator.sendMessage(
                HostMessageFactory.createRejoinAckMessage(
                    RejoinInfo(game.gameCommonId, guestPlayer, senderLocalConnectionId)
                )
            )
        }
        verify { mockHostCommunicator.sendMessage(mockWwaitingRoomStateUpdateMessageWrapper) }
        confirmVerified(mockHostCommunicator)
    }

    @Test
    fun `Add in-game ID of rejoining player to connection store`() {
        setupWaitingRoomStateUpdateMessageMock()

        launchTest()

        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isEqualTo(guestPlayer.dwitchId)
    }

    @Test
    fun `Update player in store with connection state "connected" and ready state "not ready"`() {
        mockUpdatePlayerWthConnectionStateAndReady()
        setupWaitingRoomStateUpdateMessageMock()

        launchTest()

        verify {
            mockInGameStore.updatePlayerWithConnectionStateAndReady(
                guestPlayer.id,
                PlayerConnectionState.CONNECTED,
                false
            )
        }
    }

    @Test
    fun `Kick rejoining player when it cannot be found in store`() {
        mockGuestPlayerNotFound()

        launchTest()

        verify { mockInGameStore.getPlayer(guestPlayer.dwitchId) }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()
    }

    @Test
    fun `Kick rejoining player when the provided game common ID does not match the ID of the current game`() {
        mockGuestPlayerFound()

        launchTestWithOtherGameCommonId()

        verify { mockInGameStore.getGame() }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()
    }

    private fun mockGuestPlayerFound() {
        every { mockInGameStore.getPlayer(guestPlayer.dwitchId) } returns guestPlayer
    }

    private fun mockGuestPlayerNotFound() {
        every { mockInGameStore.getPlayer(guestPlayer.dwitchId) } returns null
    }

    private fun mockUpdatePlayerWthConnectionStateAndReady() {
        every {
            mockInGameStore.updatePlayerWithConnectionStateAndReady(any(), any(), any())
        } returns 1
    }

    private fun mockGame() {
        every { mockInGameStore.getGame() } returns game
    }

    private fun launchTest() {
        processor.process(
            Message.RejoinGameMessage(game.gameCommonId, guestPlayer.dwitchId),
            senderLocalConnectionId
        ).test().assertComplete()
    }

    private fun launchTestWithOtherGameCommonId() {
        val otherGameCommonId = GameCommonId(12343)
        assertThat(otherGameCommonId).isNotEqualTo(game.gameCommonId)
        processor.process(
            Message.RejoinGameMessage(otherGameCommonId, guestPlayer.dwitchId),
            senderLocalConnectionId
        ).test().assertComplete()
    }

}