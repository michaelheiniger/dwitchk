package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.mockk.CapturingSlot
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
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

    @Test
    fun `When the local player is the host, it forwards the updated game state message`() {
        createLocalPlayer(PlayerRole.HOST)
        mockGetLocalPlayer()

        launchTest()

        val envelopeToSendCap = CapturingSlot<EnvelopeToSend>()
        verify { mockGameCommunicator.sendMessage(capture(envelopeToSendCap)) }
        val gameStateUpdatedTest = envelopeToSendCap.captured.message as Message.GameStateUpdatedMessage
        assertThat(gameStateUpdatedTest.gameState).isEqualToIgnoringGivenFields(gameState, "localPlayerInGameId")
        confirmVerified(mockGameCommunicator)
    }

    @Test
    fun `When the local player is the guest, it does not send any message in response`() {
        createLocalPlayer(PlayerRole.GUEST)
        mockGetLocalPlayer()

        launchTest()

        verify(exactly = 0) { mockGameCommunicator.sendMessage(any()) }
        confirmVerified(mockGameCommunicator)
    }

    private fun launchTest() {
        processor.process(Message.GameStateUpdatedMessage(gameState), LocalConnectionId(0))
            .test().assertComplete()
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