package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LaunchGameMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var gameEventRepository: GuestGameEventRepository

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private lateinit var processor: LaunchGameMessageProcessor

    private lateinit var message: Message.LaunchGameMessage

    @BeforeEach
    override fun setup() {
        super.setup()

        gameEventRepository = GuestGameEventRepository()
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
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(GuestGameEvent.GameLaunched)
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