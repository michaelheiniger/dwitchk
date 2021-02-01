package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.GameOverMessageProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameOverMessageProcessorTest : BaseMessageProcessorTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private lateinit var gameEventRepository: GuestGameEventRepository

    private lateinit var processor: GameOverMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GuestGameEventRepository()
        processor = GameOverMessageProcessor(mockAppEventRepository, gameEventRepository)
        setupCommunicatorSendGameState()
    }

    @Test
    fun `Service is stopped`() {
        launchTest()

        verify { mockAppEventRepository.notify(AppEvent.GameLeft) }
    }

    @Test
    fun `Notify of GameOver event`() {
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(GuestGameEvent.GameOver)
    }

    private fun launchTest() {
        processor.process(Message.GameOverMessage, ConnectionId(0))
            .test()
            .assertComplete()
    }
}