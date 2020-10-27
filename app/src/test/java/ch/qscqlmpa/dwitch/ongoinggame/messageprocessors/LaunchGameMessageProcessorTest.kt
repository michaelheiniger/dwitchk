package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LaunchGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var gameEventRepository: GameEventRepository

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private lateinit var processor: LaunchGameMessageProcessor

    private lateinit var message: Message.LaunchGameMessage

    @BeforeEach
    override fun setup() {
        super.setup()

        gameEventRepository = GameEventRepository()
        processor = LaunchGameMessageProcessor(
            mockInGameStore,
            mockServiceManager,
            gameEventRepository
        )

        message = buildMessage()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearAllMocks()
    }

    @Test
    fun `Store gamestate from message`() {
        launchTest()

        verify { mockInGameStore.updateGameState(message.gameState) }
    }

    @Test
    fun `Emit GameLaunched event`() {
        assertThat(gameEventRepository.getLastEvent()).isNull()

        launchTest()

        assertThat(gameEventRepository.getLastEvent()).isEqualTo(GameEvent.GameLaunched)
    }

    @Test
    fun `Change room to GameRoom`() {
        launchTest()

        verify { mockServiceManager.goToGuestGameRoom() }
    }

    private fun launchTest() {
        processor.process(message, LocalConnectionId(0L))
            .test().assertComplete()
    }

    private fun buildMessage(): Message.LaunchGameMessage {
        val gameState = TestEntityFactory.createGameState()
        return Message.LaunchGameMessage(gameState)
    }
}