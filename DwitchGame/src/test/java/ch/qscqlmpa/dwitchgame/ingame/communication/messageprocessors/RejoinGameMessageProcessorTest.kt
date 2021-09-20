package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.ingame.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.model.RejoinInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.TestUtil
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.ingamestore.model.GameCommonIdAndCurrentRoom
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class RejoinGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var connectionStore: ConnectionStore

    private lateinit var processor: RejoinGameMessageProcessor

    private val guestPlayerLocalId = 10102L
    private val guestPlayerDwitchId = DwitchPlayerId(32434)

    private val senderLocalConnectionId = ConnectionId(124)

    private lateinit var mockWaitingRoomStateUpdateEnvelope: EnvelopeToSend
    private lateinit var mockGameStateUpdateEnvelope: EnvelopeToSend

    private val currentGameCommonId = GameCommonId(UUID.randomUUID())

    @BeforeEach
    fun setup() {
        connectionStore = ConnectionStoreFactory.createConnectionStore()

        processor = RejoinGameMessageProcessor(
            mockInGameStore,
            TestUtil.lazyOf(mockHostCommunicator),
            mockHostMessageFactory,
            mockMessageFactory,
            connectionStore
        )
        mockGuestPlayerFound()
        mockUpdatePlayer()
        mockWaitingRoomStateUpdateEnvelope = setupWaitingRoomStateUpdateMessageMock()
        mockGameStateUpdateEnvelope = setupGameStateUpdateMessageMock()
    }

    @Test
    fun `Send rejoin game ACK when rejoining player is found in store`() {
        // Test with WaitingRoom
        launchTestWithSameGameCommonId(RoomType.WAITING_ROOM)

        verify {
            mockHostCommunicator.sendMessage(
                HostMessageFactory.createRejoinAckMessage(
                    RejoinInfo(
                        currentGameCommonId,
                        RoomType.WAITING_ROOM,
                        guestPlayerLocalId,
                        guestPlayerDwitchId,
                        senderLocalConnectionId
                    )
                )
            )
        }

        // Test with GameRoom
        launchTestWithSameGameCommonId(RoomType.GAME_ROOM)

        verify {
            mockHostCommunicator.sendMessage(
                HostMessageFactory.createRejoinAckMessage(
                    RejoinInfo(
                        currentGameCommonId,
                        RoomType.WAITING_ROOM,
                        guestPlayerLocalId,
                        guestPlayerDwitchId,
                        senderLocalConnectionId
                    )
                )
            )
        }
    }

    @Test
    fun `Send waitingroom state update message when rejoining player is found in store and current room is waitingroom`() {
        launchTestWithSameGameCommonId(RoomType.WAITING_ROOM)

        verify { mockHostCommunicator.sendMessage(mockWaitingRoomStateUpdateEnvelope) }
    }

    @Test
    fun `Send waitingroom state update and game state update messages when rejoining player is found in store and current room is gameroom`() {
        launchTestWithSameGameCommonId(RoomType.GAME_ROOM)

        verify { mockHostCommunicator.sendMessage(mockWaitingRoomStateUpdateEnvelope) }
        verify { mockHostCommunicator.sendMessage(mockGameStateUpdateEnvelope) }
    }

    @Test
    fun `Add in-game ID of rejoining player to connection store`() {
        // Test with WaitingRoom
        launchTestWithSameGameCommonId(RoomType.GAME_ROOM)

        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isEqualTo(guestPlayerDwitchId)

        // Test with GameRoom
        launchTestWithSameGameCommonId(RoomType.GAME_ROOM)

        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isEqualTo(guestPlayerDwitchId)
    }

    @Test
    fun `Update player in store with connection state connected and ready state not ready when current room is waitingroom`() {
        launchTestWithSameGameCommonId(RoomType.WAITING_ROOM)

        verify { mockInGameStore.updatePlayerWithConnectionStateAndReady(guestPlayerLocalId, connected = true, ready = false) }
    }

    @Test
    fun `Update player in store with connection state connected when current room is gameroom`() {
        launchTestWithSameGameCommonId(RoomType.GAME_ROOM)

        verify { mockInGameStore.updatePlayerWithConnectionState(guestPlayerLocalId, connected = true) }
    }

    @Test
    fun `Kick rejoining player when it cannot be found in store - waitingroom`() {
        mockGuestPlayerNotFound()
        launchTestWithSameGameCommonId(RoomType.WAITING_ROOM)

        verify { mockInGameStore.getGameCommonIdAndCurrentRoom() }
        verify { mockInGameStore.getPlayerLocalId(guestPlayerDwitchId) }
        confirmVerified(mockInGameStore)
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()
    }

    @Test
    fun `Kick rejoining player when it cannot be found in store - gameroom`() {
        mockGuestPlayerNotFound()
        launchTestWithSameGameCommonId(RoomType.GAME_ROOM)

        verify { mockInGameStore.getGameCommonIdAndCurrentRoom() }
        verify { mockInGameStore.getPlayerLocalId(guestPlayerDwitchId) }
        confirmVerified(mockInGameStore)
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()
    }

    @Test
    fun `Kick rejoining player when the provided game common ID does not match the ID of the current game - waitingroom`() {
        launchTestWithOtherGameCommonId(RoomType.WAITING_ROOM)

        verify { mockInGameStore.getGameCommonIdAndCurrentRoom() }
        confirmVerified(mockInGameStore)
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()
    }

    @Test
    fun `Kick rejoining player when the provided game common ID does not match the ID of the current game - gameroom`() {
        launchTestWithOtherGameCommonId(RoomType.GAME_ROOM)

        verify { mockInGameStore.getGameCommonIdAndCurrentRoom() }
        confirmVerified(mockInGameStore)
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(connectionStore.getDwitchId(senderLocalConnectionId)).isNull()
    }

    private fun mockGuestPlayerFound() {
        every { mockInGameStore.getPlayerLocalId(guestPlayerDwitchId) } returns guestPlayerLocalId
    }

    private fun mockGuestPlayerNotFound() {
        every { mockInGameStore.getPlayerLocalId(guestPlayerDwitchId) } returns null
    }

    private fun mockUpdatePlayer() {
        every { mockInGameStore.updatePlayerWithConnectionStateAndReady(any(), any(), any()) } returns 1
        every { mockInGameStore.updatePlayerWithConnectionState(any(), any()) } returns 1
    }

    private fun launchTestWithSameGameCommonId(currentRoom: RoomType) {
        every { mockInGameStore.getGameCommonIdAndCurrentRoom() } returns GameCommonIdAndCurrentRoom(
            currentGameCommonId,
            currentRoom
        )
        processor.process(Message.RejoinGameMessage(currentGameCommonId, guestPlayerDwitchId), senderLocalConnectionId)
            .test().assertComplete()
    }

    private fun launchTestWithOtherGameCommonId(currentRoom: RoomType) {
        val otherGameCommonId = GameCommonId(UUID.randomUUID())
        every { mockInGameStore.getGameCommonIdAndCurrentRoom() } returns GameCommonIdAndCurrentRoom(
            currentGameCommonId,
            currentRoom
        )
        assertThat(otherGameCommonId).isNotEqualTo(currentGameCommonId)
        processor.process(Message.RejoinGameMessage(otherGameCommonId, guestPlayerDwitchId), senderLocalConnectionId)
            .test().assertComplete()
    }
}
