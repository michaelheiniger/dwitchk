package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameOverMessageProcessorTest : BaseMessageProcessorTest() {

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private lateinit var gameEventRepository: GameEventRepository

    private lateinit var processor: GameOverMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GameEventRepository()
        processor = GameOverMessageProcessor(mockServiceManager, gameEventRepository)
        setupCommunicatorSendGameState()
    }

    @Test
    fun `Service is stopped`() {
        launchTest().test().assertComplete()

        verify { mockServiceManager.stopGuestService() }
    }

    @Test
    fun `Notify of GameOver event`() {
        assertThat(gameEventRepository.getLastEvent()).isNull()

        launchTest().test().assertComplete()

        assertThat(gameEventRepository.getLastEvent()).isEqualTo(GameEvent.GameOver)
    }

    private fun launchTest(): Completable {
        return processor.process(Message.GameOverMessage, LocalConnectionId(0))
    }
}