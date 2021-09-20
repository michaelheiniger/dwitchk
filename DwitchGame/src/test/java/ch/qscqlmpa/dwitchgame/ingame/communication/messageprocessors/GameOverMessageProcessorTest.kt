package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameOverMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var gameEventRepository: GuestGameEventRepository
    private val mockGuestGameLifecycleEventRepository = mockk<GuestGameLifecycleEventRepository>(relaxed = true)

    private lateinit var processor: GameOverMessageProcessor

    @BeforeEach
    fun setup() {
        gameEventRepository = GuestGameEventRepository()
        processor = GameOverMessageProcessor(gameEventRepository, mockGuestGameLifecycleEventRepository)
    }

    @Test
    fun `Notify of GameOver event`() {
        // Given
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        // When
        launchTest()

        // Then
        testObserver.assertValue(GuestGameEvent.GameOver)
        verify { mockGuestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver) }
    }

    private fun launchTest() {
        processor.process(Message.GameOverMessage, ConnectionId(0)).test().assertComplete()
    }
}
