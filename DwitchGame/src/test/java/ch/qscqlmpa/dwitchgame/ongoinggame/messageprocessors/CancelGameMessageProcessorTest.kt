package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.GameCanceledMessageProcessor
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEventRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CancelGameMessageProcessorTest : BaseMessageProcessorTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private val mockGameEventRepository = mockk<GuestGameEventRepository>(relaxed = true)

    private lateinit var processor: GameCanceledMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        processor = GameCanceledMessageProcessor(mockInGameStore, mockAppEventRepository, mockGameEventRepository)
    }

    @Test
    fun `New game is canceled`() {
        every { mockInGameStore.gameIsNew() } returns true
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        verify { mockInGameStore.deleteGame() }
        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
        verify { mockAppEventRepository.notify(AppEvent.GameLeft) }
    }

    @Test
    fun `Existing game is canceled`() {
        every { mockInGameStore.gameIsNew() } returns false
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
        verify { mockAppEventRepository.notify(AppEvent.GameLeft) }
    }
}
