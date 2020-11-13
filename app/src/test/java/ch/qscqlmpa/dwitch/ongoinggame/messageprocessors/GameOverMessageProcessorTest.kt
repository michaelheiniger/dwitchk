package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameOverMessageProcessorTest : BaseMessageProcessorTest() {

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private lateinit var gameEventRepository: GuestGameEventRepository

    private lateinit var processor: GameOverMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GuestGameEventRepository()
        processor = GameOverMessageProcessor(mockServiceManager, gameEventRepository)
        setupCommunicatorSendGameState()
    }

    @Test
    fun `Service is stopped`() {
        launchTest()

        verify { mockServiceManager.stopGuestService() }
    }

    @Test
    fun `Notify of GameOver event`() {
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(GuestGameEvent.GameOver)
    }

    private fun launchTest() {
        processor.process(Message.GameOverMessage, LocalConnectionId(0))
            .test()
            .assertComplete()
    }
}