package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GuestDisconnectedFromHostEventProcessorTest : BaseUnitTest() {

    private lateinit var commStateRepository: GuestCommunicationStateRepository

    private lateinit var processorGuest: GuestDisconnectedFromHostEventProcessor

    @BeforeEach
    fun setup() {
        commStateRepository = GuestCommunicationStateRepository()
        processorGuest = GuestDisconnectedFromHostEventProcessor(commStateRepository)
    }

    @Test
    fun `Notify that Guest communication state is now Disconnected`() {
        launchTest()

        assertThat(commStateRepository.currentState().blockingFirst()).isEqualTo(GuestCommunicationState.Disconnected)
    }

    private fun launchTest() {
        processorGuest.process(ClientEvent.CommunicationEvent.DisconnectedFromHost).test().assertComplete()
    }
}
