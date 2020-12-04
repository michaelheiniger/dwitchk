package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.LaunchGameMessageProcessor
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LaunchGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var gameEventRepository: GuestGameEventRepository

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private lateinit var processor: LaunchGameMessageProcessor

    private lateinit var message: Message.LaunchGameMessage

    @BeforeEach
    override fun setup() {
        super.setup()

        gameEventRepository = GuestGameEventRepository()
        processor = LaunchGameMessageProcessor(
            mockInGameStore,
            mockAppEventRepository,
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

        verify { mockAppEventRepository.notify(AppEvent.GameRoomJoinedByGuest) }
    }

    private fun launchTest() {
        processor.process(message, ConnectionId(0L))
            .test().assertComplete()
    }

    private fun buildMessage(): Message.LaunchGameMessage {
        val gameState = TestEntityFactory.createGameState()
        return Message.LaunchGameMessage(gameState)
    }
}