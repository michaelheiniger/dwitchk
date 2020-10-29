package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.utils.TestUtil
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RejoinGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var localConnectionIdStore: LocalConnectionIdStore

    private lateinit var processor: RejoinGameMessageProcessor

    private val guestPlayer = TestEntityFactory.createGuestPlayer1()

    private val senderLocalConnectionId = LocalConnectionId(0)

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
        setupCommunicatorSendMessageCompleteMock()
    }

    @Test
    fun `Send waiting room state update message when rejoining player is found in store`() {
        every { mockInGameStore.getPlayer(guestPlayer.inGameId) } returns guestPlayer
        every { mockInGameStore.updatePlayerWithConnectionStateAndReady(any(), any(), any()) } returns 1
        val waitingRoomStateUpdateMessageWrapperMock = setupWaitingRoomStateUpdateMessageMock()

        launchTest().test().assertComplete()

        verify { mockHostCommunicator.sendMessage(waitingRoomStateUpdateMessageWrapperMock) }
    }

    @Test
    fun `Add in-game ID of rejoining player to connection store`() {
        every { mockInGameStore.getPlayer(guestPlayer.inGameId) } returns guestPlayer
        every { mockInGameStore.updatePlayerWithConnectionStateAndReady(any(), any(), any()) } returns 1
        setupWaitingRoomStateUpdateMessageMock()

        launchTest().test().assertComplete()

        assertThat(localConnectionIdStore.getInGameId(senderLocalConnectionId)).isEqualTo(guestPlayer.inGameId)
    }

    @Test
    fun `Update player in store with connection state "connected" and ready state "not ready"`() {

        every { mockInGameStore.getPlayer(guestPlayer.inGameId) } returns guestPlayer
        every { mockInGameStore.updatePlayerWithConnectionStateAndReady(any(), any(), any()) } returns 1

        setupWaitingRoomStateUpdateMessageMock()

        launchTest().test().assertComplete()

        verify { mockInGameStore.updatePlayerWithConnectionStateAndReady(guestPlayer.id, PlayerConnectionState.CONNECTED, false) }
    }

    @Test
    fun `Kick rejoining player when it cannot be found in store`() {
        every { mockInGameStore.getPlayer(guestPlayer.inGameId) } returns null // Player can't be found in store

        launchTest().test().assertComplete()

        verify { mockInGameStore.getPlayer(guestPlayer.inGameId) }
        verify { mockHostCommunicator.closeConnectionWithClient(senderLocalConnectionId) }
        confirmVerified(mockInGameStore)
        confirmVerified(mockHostCommunicator)
        assertThat(localConnectionIdStore.getInGameId(senderLocalConnectionId)).isNull()
    }

    private fun launchTest(): Completable {
        return processor.process(Message.RejoinGameMessage(guestPlayer.inGameId), senderLocalConnectionId)
    }
}