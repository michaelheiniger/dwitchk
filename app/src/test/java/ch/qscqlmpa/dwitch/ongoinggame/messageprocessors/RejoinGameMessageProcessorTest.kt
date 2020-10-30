package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.utils.TestUtil
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RejoinGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var localConnectionIdStore: LocalConnectionIdStore

    private lateinit var processor: RejoinGameMessageProcessor

    private val game = TestEntityFactory.createGameInWaitingRoom()

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    private val senderLocalConnectionId = LocalConnectionId(0)

    private lateinit var mockWwaitingRoomStateUpdateMessageWrapper: EnvelopeToSend

    @BeforeEach
    override fun setup() {
        super.setup()
        localConnectionIdStore = LocalConnectionIdStore()

        processor = RejoinGameMessageProcessor(
            mockInGameStore,
            TestUtil.lazyOf(mockHostCommunicator),
            mockHostMessageFactory,
            localConnectionIdStore
        )
        mockGame()
        mockGuestPlayerFound()
        mockUpdatePlayerWthConnectionStateAndReady()
        setupCommunicatorSendMessageCompleteMock()
        mockWwaitingRoomStateUpdateMessageWrapper = setupWaitingRoomStateUpdateMessageMock()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
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

        assertThat(localConnectionIdStore.getInGameId(senderLocalConnectionId)).isEqualTo(
            guestPlayer.inGameId
        )
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

        verify { mockInGameStore.getPlayer(guestPlayer.inGameId) }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(localConnectionIdStore.getInGameId(senderLocalConnectionId)).isNull()
    }

    @Test
    fun `Kick rejoining player when the provided game common ID does not match the ID of the current game`() {
        mockGuestPlayerFound()

        launchTestWithOtherGameCommonId()

        verify { mockInGameStore.getGame() }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockHostCommunicator)
        assertThat(localConnectionIdStore.getInGameId(senderLocalConnectionId)).isNull()
    }

    private fun mockGuestPlayerFound() {
        every { mockInGameStore.getPlayer(guestPlayer.inGameId) } returns guestPlayer
    }

    private fun mockGuestPlayerNotFound() {
        every { mockInGameStore.getPlayer(guestPlayer.inGameId) } returns null
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
            Message.RejoinGameMessage(game.gameCommonId, guestPlayer.inGameId),
            senderLocalConnectionId
        ).test().assertComplete()
    }

    private fun launchTestWithOtherGameCommonId() {
        val otherGameCommonId = GameCommonId(12343)
        assertThat(otherGameCommonId).isNotEqualTo(game.gameCommonId)
        processor.process(
            Message.RejoinGameMessage(otherGameCommonId, guestPlayer.inGameId),
            senderLocalConnectionId
        ).test().assertComplete()
    }

}