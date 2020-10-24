package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.mockk.CapturingSlot
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameOverMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var gameEventRepository: GameEventRepository

    private lateinit var processor: GameOverMessageProcessor

    private lateinit var localPlayer: Player

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GameEventRepository()
        processor = GameOverMessageProcessor(
            mockInGameStore,
            gameEventRepository,
            mockGameCommunicator
        )
        setupCommunicatorSendGameState()
    }

    @Test
    fun `When the local player is the host, it forwards the GameOver message to all guests`() {
        createLocalPlayer(PlayerRole.HOST)
        mockGetLocalPlayer()

        launchTest().test().assertComplete()

        val envelopeToSendCap = CapturingSlot<EnvelopeToSend>()
        verify { mockGameCommunicator.sendMessage(capture(envelopeToSendCap)) }

        val messageSent = envelopeToSendCap.captured.message
        assertThat(messageSent).isInstanceOf(Message.GameOverMessage::class.java)
        confirmVerified(mockGameCommunicator)
    }

    @Test
    fun `When the local player is the guest, it does not send any message in response`() {
        createLocalPlayer(PlayerRole.GUEST)
        mockGetLocalPlayer()

        launchTest().test().assertComplete()

        verify(exactly = 0) { mockGameCommunicator.sendMessage(any()) }
        confirmVerified(mockGameCommunicator)
    }

    private fun launchTest(): Completable {
        return processor.process(Message.GameOverMessage, LocalConnectionId(0))
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