package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.TestEntityFactory
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.mockk.*
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameStateUpdatedMessageProcessorTest : BaseMessageProcessorTest() {

    private val gameState: GameState = TestEntityFactory.createGameState()

    private lateinit var localPlayer: Player

    private lateinit var processor: GameStateUpdatedMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        processor = GameStateUpdatedMessageProcessor(mockInGameStore, mockGameCommunicator)
        setupCommunicatorSendGameState()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockGameCommunicator)
    }

    @Test
    fun `The received GameState is stored in the local store with the correct local (host) player in-game ID`() {
        testReceivedGameStateIsStoredInTheLocalStore(PlayerRole.HOST)
    }

    @Test
    fun `The received GameState is stored in the local store with the correct local (guest) player in-game ID`() {
        testReceivedGameStateIsStoredInTheLocalStore(PlayerRole.GUEST)
    }

    @Test
    fun `When the local player is the host, it forwards the updated game state message`() {
        createLocalPlayer(PlayerRole.HOST)
        mockGetLocalPlayer()

        launchTest().test().assertComplete()

        val envelopeToSendCap = CapturingSlot<EnvelopeToSend>()
        verify { mockGameCommunicator.sendGameState(capture(envelopeToSendCap)) }
        val gameStateUpdatedTest = envelopeToSendCap.captured.message as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedTest.gameState).isEqualToIgnoringGivenFields(gameState, "localPlayerInGameId")
        confirmVerified(mockGameCommunicator)
    }

    @Test
    fun `When the local player is the guest, it does not send any message in response`() {
        createLocalPlayer(PlayerRole.GUEST)
        mockGetLocalPlayer()

        launchTest().test().assertComplete()

        verify(exactly = 0) { mockGameCommunicator.sendGameState(any()) }
        confirmVerified(mockGameCommunicator)
    }

    private fun testReceivedGameStateIsStoredInTheLocalStore(localPlayerRole: PlayerRole) {
        createLocalPlayer(localPlayerRole)
        mockGetLocalPlayer()

        assertThat(gameState.localPlayerId).isNotEqualTo(localPlayer.inGameId)

        launchTest().test().assertComplete()

        verify { mockInGameStore.updateGameState(gameState.copy(localPlayerId = localPlayer.inGameId)) }
    }

    private fun launchTest(): Completable {
        return processor.process(Message.GameStateUpdatedMessage(gameState), LocalConnectionId(0))
    }

    private fun createLocalPlayer(localPlayerRole: PlayerRole) {
        localPlayer = when (localPlayerRole) {
            PlayerRole.GUEST -> TestEntityFactory.createGuestPlayer1()
            PlayerRole.HOST -> TestEntityFactory.createHostPlayer(inGameId = PlayerInGameId(10000L))
        }
    }

    private fun mockGetLocalPlayer() {
        every { mockInGameStore.getLocalPlayer() } returns localPlayer
    }
}