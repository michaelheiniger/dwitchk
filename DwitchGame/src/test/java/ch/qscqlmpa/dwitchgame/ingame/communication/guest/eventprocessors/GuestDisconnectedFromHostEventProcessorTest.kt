package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GuestDisconnectedFromHostEventProcessorTest : BaseUnitTest() {

    private val mockCommStateRepository = mockk<GuestCommunicationStateRepository>(relaxed = true)

    private lateinit var processorGuest: GuestDisconnectedFromHostEventProcessor

    @BeforeEach
    fun setup() {
        processorGuest = GuestDisconnectedFromHostEventProcessor(mockCommStateRepository)
    }

    @Test
    fun `Notify that Guest communication state is now Disconnected`() {
        // Given
        val eventToProcess = ClientEvent.CommunicationEvent.DisconnectedFromServer

        // When
        processorGuest.process(eventToProcess).test().assertComplete()

        // Then
        verify { mockCommStateRepository.notifyEvent(eventToProcess) }
    }
}
