package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class KickPlayerMessageProcessorTest : BaseMessageProcessorTest() {

    private val mockGameEventRepository = mockk<GuestGameEventRepository>(relaxed = true)
    private val mockGameLifecycleEventRepository = mockk<GuestGameLifecycleEventRepository>(relaxed = true)

    private lateinit var processor: KickPlayerMessageProcessor

    @BeforeEach
    fun setup() {
        processor = KickPlayerMessageProcessor(
            mockInGameStore,
            mockGameEventRepository,
            mockGameLifecycleEventRepository
        )
    }

    @Test
    fun `Local player is kicked from new game`() {
        // Given
        every { mockInGameStore.gameIsNew() } returns true

        // When
        processor.process(Message.KickPlayerMessage(DwitchPlayerId(1)), ConnectionId(0)).test().assertComplete()

        // Then
        verify { mockInGameStore.markGameForDeletion() }
        verify { mockGameEventRepository.notify(GuestGameEvent.KickedOffGame) }
        verify { mockGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver) }
    }

    @Test
    fun `Local player is kicked from existing game`() {
        // Given
        every { mockInGameStore.gameIsNew() } returns false

        // When
        processor.process(Message.KickPlayerMessage(DwitchPlayerId(1)), ConnectionId(0)).test().assertComplete()

        // Then
        verify(exactly = 0) { mockInGameStore.markGameForDeletion() }
        verify { mockGameEventRepository.notify(GuestGameEvent.KickedOffGame) }
        verify { mockGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver) }
    }
}
