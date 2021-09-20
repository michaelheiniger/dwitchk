package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LaunchGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var gameEventRepository: GuestGameEventRepository
    private val mockGameLifecycleEventRepository = mockk<GuestGameLifecycleEventRepository>(relaxed = true)

    private lateinit var processor: LaunchGameMessageProcessor

    private lateinit var message: Message.LaunchGameMessage

    @BeforeEach
    fun setup() {
        gameEventRepository = GuestGameEventRepository()
        processor = LaunchGameMessageProcessor(
            mockInGameStore,
            mockGameLifecycleEventRepository,
            gameEventRepository
        )

        message = buildMessage()
    }

    @Test
    fun `Store gamestate from message`() {
        launchTest()

        verify { mockInGameStore.updateGameState(message.gameState) }
    }

    @Test
    fun `Emit GameLaunched event`() {
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(GuestGameEvent.GameLaunched)
    }

    @Test
    fun `Change room to GameRoom`() {
        launchTest()

        verify { mockGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.MovedToGameRoom) }
    }

    private fun launchTest() {
        processor.process(message, ConnectionId(0L)).test().assertComplete()
    }

    private fun buildMessage(): Message.LaunchGameMessage {
        val gameState = TestEntityFactory.createGameState()
        return Message.LaunchGameMessage(gameState)
    }
}
