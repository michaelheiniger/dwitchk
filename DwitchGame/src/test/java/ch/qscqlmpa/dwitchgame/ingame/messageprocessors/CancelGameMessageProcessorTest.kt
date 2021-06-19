package ch.qscqlmpa.dwitchgame.ingame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.GameCanceledMessageProcessor
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CancelGameMessageProcessorTest : BaseMessageProcessorTest() {

    private val gameLifecycleEventRepository = mockk<GuestGameLifecycleEventRepository>(relaxed = true)

    private val mockGameEventRepository = mockk<GuestGameEventRepository>(relaxed = true)

    private lateinit var processor: GameCanceledMessageProcessor

    @BeforeEach
    fun setup() {
        processor = GameCanceledMessageProcessor(mockInGameStore, gameLifecycleEventRepository, mockGameEventRepository)
    }

    @Test
    fun `New game is canceled`() {
        // Given
        every { mockInGameStore.gameIsNew() } returns true

        // When
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        // Then
        verify { mockInGameStore.deleteGame() }
        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
        verify { gameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GuestLeftGame) }
    }

    @Test
    fun `Existing game is canceled`() {
        // Given
        every { mockInGameStore.gameIsNew() } returns false

        // When
        processor.process(Message.CancelGameMessage, ConnectionId(0)).test().assertComplete()

        // Then
        verify { mockGameEventRepository.notify(GuestGameEvent.GameCanceled) }
        verify { gameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GuestLeftGame) }
    }
}
