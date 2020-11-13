package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GuestConnectionErrorEventProcessorTest : BaseUnitTest() {

    private lateinit var commEventRepository: GuestCommunicationEventRepository

    private lateinit var processorGuest: GuestConnectionErrorEventProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        commEventRepository = GuestCommunicationEventRepository()
        processorGuest = GuestConnectionErrorEventProcessor(commEventRepository)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `Notify that Guest communication state is now Error`() {
        processorGuest.process(ClientCommunicationEvent.ConnectionError("Error"))
            .test().assertComplete()

        assertThat(commEventRepository.consumeLastEvent()).isEqualTo(GuestCommunicationState.Error)
    }
}