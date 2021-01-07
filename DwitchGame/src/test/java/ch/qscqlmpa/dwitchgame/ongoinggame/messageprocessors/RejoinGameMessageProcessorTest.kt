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
import ch.qscqlmpa.dwitchmodel.game.RoomType
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

    private val senderLocalConnectionId = ConnectionId(124)

    private lateinit var mockWaitingRoomStateUpdateEnvelope: EnvelopeToSend
    private lateinit var mockGameStateUpdateEnvelope: EnvelopeToSend

    @BeforeEach
    override fun setup() {
        super.setup()
        connectionStore = ConnectionStoreFactory.createConnectionStore()

        processor = RejoinGameMessageProcessor(
            mockInGameStore,
            TestUtil.lazyOf(mockHostCommunicator),
            mockHostMessageFactory,
            mockMessageFactory,
            connectionStore
        )
        mockGame()
        mockGuestPlayerFound()
        mockUpdatePlayer()
        setupCommunicatorSendMessageCompleteMock()
        mockWaitingRoomStateUpdateEnvelope = setupWaitingRoomStateUpdateMessageMock()
        mockGameStateUpdateEnvelope = setupGameStateUpdateMessageMock()
    }

    @Test
    fun `Send rejoin game ACK when rejoining player is found in store`() {
        // Test with WaitingRoom
        mockCurrentRoom(RoomType.WAITING_ROOM)

        launchTest()

        verify {
            mockHostCommunicator.sendMessage(
                HostMessageFactory.createRejoinAckMessage(RejoinInfo(game.gameCommonId, guestPlayer, senderLocalConnectionId))
            )
        }

        // Test with GameRoom
        mockCurrentRoom(RoomType.GAME_ROOM)

        launchTest()

        verify {
            mockHostCommunicator.sendMessage(
                HostMessageFactory.createRejoinAckMessage(RejoinInfo(game.gameCommonId, guestPlayer, senderLocalConnectionId))
            )
        }
    }

    @Test
    fun `Send waitingroom state update message when rejoining player is found in store and current room is waitingroom`() {
        mockCurrentRoom(RoomType.WAITING_ROOM)

        launchTest()

        verify { mockHostCommunicator.sendMessage(mockWaitingRoomStateUpdateEnvelope) }
    }

    @Test
    fun `Send game state update message when rejoining player is found in store and current room is gameroom`() {
        mockCurrentRoom(RoomType.GAME_ROOM)

        launchTest()

        verify { mockHostCommunicator.sendMessage(mockGameStateUpdateEnvelope) }
    }

    @Test
    fun `Add in-game ID of rejoining player to connection store`() {
        // Test with WaitingRoom
        mockCurrentRoom(RoomType.GAME_ROOM)

        launchTest()

        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isEqualTo(guestPlayer.dwitchId)

        // Test with GameRoom
        mockCurrentRoom(RoomType.GAME_ROOM)

        launchTest()

        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isEqualTo(guestPlayer.dwitchId)
    }

    @Test
    fun `Update player in store with connection state connected and ready state not ready when current room is Waitingroom`() {
        mockCurrentRoom(RoomType.WAITING_ROOM)

        launchTest()

        verify { mockInGameStore.updatePlayerWithConnectionStateAndReady(guestPlayer.id, PlayerConnectionState.CONNECTED, false) }
    }

    @Test
    fun `Update player in store with connection state connected when current room is Gameroom`() {
        mockCurrentRoom(RoomType.GAME_ROOM)

        launchTest()

        verify { mockInGameStore.updatePlayerWithConnectionState(guestPlayer.id, PlayerConnectionState.CONNECTED) }
    }

    @Test
    fun `Kick rejoining player when it cannot be found in store`() {
        // Test with WaitingRoom
        mockCurrentRoom(RoomType.WAITING_ROOM)
        mockGuestPlayerNotFound()

        launchTest()

        verify { mockInGameStore.getPlayer(guestPlayer.dwitchId) }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()

        // Test with GameRoom
        mockCurrentRoom(RoomType.GAME_ROOM)
        mockGuestPlayerNotFound()

        launchTest()

        verify { mockInGameStore.getPlayer(guestPlayer.dwitchId) }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()
    }

    @Test
    fun `Kick rejoining player when the provided game common ID does not match the ID of the current game`() {
        // Test with WaitingRoom
        mockCurrentRoom(RoomType.WAITING_ROOM)

        launchTestWithOtherGameCommonId()

        verify { mockInGameStore.getGame() }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()

        // Test with GameRoom
        mockCurrentRoom(RoomType.GAME_ROOM)

        launchTestWithOtherGameCommonId()

        verify { mockInGameStore.getGame() }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()
    }

    private fun mockCurrentRoom(room: RoomType) {
        every { mockInGameStore.getCurrentRoom() } returns room
    }

    private fun mockGuestPlayerFound() {
        every { mockInGameStore.getPlayer(guestPlayer.dwitchId) } returns guestPlayer
    }

    private fun mockGuestPlayerNotFound() {
        every { mockInGameStore.getPlayer(guestPlayer.dwitchId) } returns null
    }

    private fun mockUpdatePlayer() {
        every { mockInGameStore.updatePlayerWithConnectionStateAndReady(any(), any(), any()) } returns 1
        every { mockInGameStore.updatePlayerWithConnectionState(any(), any()) } returns 1
    }

    private fun mockGame() {
        every { mockInGameStore.getGame() } returns game
    }

    private fun launchTest() {
        processor.process(Message.RejoinGameMessage(game.gameCommonId, guestPlayer.dwitchId), senderLocalConnectionId)
            .test().assertComplete()
    }

    private fun launchTestWithOtherGameCommonId() {
        val otherGameCommonId = GameCommonId(12343)
        assertThat(otherGameCommonId).isNotEqualTo(game.gameCommonId)
        processor.process(Message.RejoinGameMessage(otherGameCommonId, guestPlayer.dwitchId), senderLocalConnectionId)
            .test().assertComplete()
    }

}