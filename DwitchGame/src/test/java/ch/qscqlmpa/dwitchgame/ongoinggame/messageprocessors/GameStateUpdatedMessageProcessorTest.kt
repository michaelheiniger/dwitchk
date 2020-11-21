package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.GameStateUpdatedMessageProcessor
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.mockk.CapturingSlot
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
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