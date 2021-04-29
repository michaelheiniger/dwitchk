package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import io.mockk.confirmVerified
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GuestDisconnectedFromHostEventProcessorTest : BaseUnitTest() {

    private lateinit var commStateRepository: GuestCommunicationStateRepository

    private lateinit var processorGuest: GuestDisconnectedFromHostEventProcessor

    @BeforeEach
    fun setup() {
        commStateRepository = GuestCommunicationStateRepository()
        processorGuest = GuestDisconnectedFromHostEventProcessor(mockInGameStore, commStateRepository)
    }

    @Test
    fun `Notify that Guest communication state is now Disconnected`() {
        launchTest()

        assertThat(commStateRepository.currentState().blockingFirst()).isEqualTo(GuestCommunicationState.Disconnected)
    }

    @Test
    fun `Set all other players' state to disconnected in store`() {
        launchTest()

        verify { mockInGameStore.setAllPlayersToDisconnected() }
        confirmVerified(mockInGameStore)
    }

    private fun launchTest() {
        processorGuest.process(ClientCommunicationEvent.DisconnectedFromHost).test().assertComplete()
    }
}
