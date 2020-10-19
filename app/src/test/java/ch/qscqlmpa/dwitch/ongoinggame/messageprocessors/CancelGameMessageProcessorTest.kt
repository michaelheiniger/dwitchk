package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CancelGameMessageProcessorTest : BaseMessageProcessorTest() {

    private val mockGameEventRepository = mockk<GameEventRepository>(relaxed = true)

    private lateinit var processor: CancelGameMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        processor = CancelGameMessageProcessor(mockInGameStore, mockGameEventRepository)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockGameEventRepository)
    }

    @Test
    fun `Delete game from store`() {
        processor.process(Message.CancelGameMessage, LocalConnectionId(0)).test().assertComplete()

        verify { mockInGameStore.deleteGame() }
    }

    @Test
    fun `Notify of "game canceled" game event`() {
        processor.process(Message.CancelGameMessage, LocalConnectionId(0)).test().assertComplete()

        verify { mockGameEventRepository.notifyOfEvent(GameEvent.GameCanceled) }
    }
}