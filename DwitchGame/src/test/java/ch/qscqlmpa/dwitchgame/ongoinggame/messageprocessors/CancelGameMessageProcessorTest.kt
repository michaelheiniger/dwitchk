package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.CancelGameMessageProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import io.mockk.every
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
    fun `New game is canceled`() {
        every { mockInGameStore.gameIsNew() } returns true
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        verify { mockInGameStore.deleteGame() }
        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
    }

    @Test
    fun `Existing game is canceled`() {
        every { mockInGameStore.gameIsNew() } returns false
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
    }
}
