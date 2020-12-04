package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.CancelGameMessageProcessor
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CancelGameMessageProcessorTest : BaseMessageProcessorTest() {

    private val mockGameEventRepository = mockk<GuestGameEventRepository>(relaxed = true)

    private lateinit var processor: CancelGameMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        processor = CancelGameMessageProcessor(mockInGameStore, mockGameEventRepository)
    }

    @Test
    fun `Delete game from store`() {
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        verify { mockInGameStore.deleteGame() }
    }

    @Test
    fun `Notify of "game canceled" game event`() {
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
    }
}