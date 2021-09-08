package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameCanceledMessageProcessorTest : BaseMessageProcessorTest() {

    private val mockGameEventRepository = mockk<GuestGameEventRepository>(relaxed = true)
    private val mockGameLifecycleEventRepository = mockk<GuestGameLifecycleEventRepository>(relaxed = true)

    private lateinit var processor: GameCanceledMessageProcessor

    @BeforeEach
    fun setup() {
        processor = GameCanceledMessageProcessor(mockInGameStore, mockGameEventRepository, mockGameLifecycleEventRepository)
    }

    @Test
    fun `New game is canceled`() {
        // Given
        every { mockInGameStore.gameIsNew() } returns true

        // When
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        // Then
        verify { mockInGameStore.markGameForDeletion() }
        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
        verify { mockGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver) }
    }

    @Test
    fun `Existing game is canceled`() {
        // Given
        every { mockInGameStore.gameIsNew() } returns false

        // When
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        // Then
        verify(exactly = 0) { mockInGameStore.markGameForDeletion() }
        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
        verify { mockGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver) }
    }
}
