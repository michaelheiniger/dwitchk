package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestConnectionErrorEventProcessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GuestConnectionErrorEventProcessorTest : BaseUnitTest() {

    private lateinit var commStateRepository: GuestCommunicationStateRepository

    private lateinit var processorGuest: GuestConnectionErrorEventProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        commStateRepository = GuestCommunicationStateRepository()
        processorGuest = GuestConnectionErrorEventProcessor(commStateRepository)
    }

    @Test
    fun `Notify that Guest communication state is now Error`() {
        processorGuest.process(ClientCommunicationEvent.ConnectionError("Error"))
            .test().assertComplete()

        assertThat(commStateRepository.observeEvents().blockingFirst()).isEqualTo(GuestCommunicationState.Error)
    }
}