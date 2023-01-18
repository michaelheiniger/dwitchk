package ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.usecases.ResumeComputerPlayersUsecase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.tinylog.kotlin.Logger

internal class HostListeningForConnectionsEventProcessorTest : BaseUnitTest() {
    private lateinit var connectionStore: ConnectionStore
    private val mockCommunicationStateRepository = mockk<HostCommunicationStateRepository>(relaxed = true)
    private val mockResumeComputerPlayersUsecase = mockk<ResumeComputerPlayersUsecase>(relaxed = true)

    private lateinit var processor: HostListeningForConnectionsEventProcessor

    private val hostDwitchId = DwitchPlayerId(1)

    @BeforeEach
    fun setup() {
        connectionStore = ConnectionStoreFactory.createConnectionStore()
        processor = HostListeningForConnectionsEventProcessor(
            mockInGameStore,
            connectionStore,
            mockCommunicationStateRepository,
            mockResumeComputerPlayersUsecase
        )
        every { mockInGameStore.getLocalPlayerDwitchId() } returns hostDwitchId
        every { mockInGameStore.gameIsNotNew() } returns false
    }

    @Test
    fun `DwitchId of the host must be paired to its connection ID`() {
        // When
        launchTest()

        // Then
        assertThat(connectionStore.getDwitchId(ConnectionStore.hostConnectionId)).isEqualTo(hostDwitchId)
        assertThat(connectionStore.getConnectionId(hostDwitchId)).isEqualTo(ConnectionStore.hostConnectionId)
    }

    @Test
    fun `Communication state repository is notified of the new state`() {
        // When
        launchTest()

        // Then
        verify { mockCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.ListeningForConnections) }
        confirmVerified(mockCommunicationStateRepository)
    }

    @Test
    fun `When the game is a new game, there is computer players to resume`() {
        // Given
        every { mockInGameStore.gameIsNotNew() } returns false

        // When
        launchTest()

        // Then
        verify(exactly = 0) { mockResumeComputerPlayersUsecase.resumeComputerPlayers() }
        confirmVerified(mockResumeComputerPlayersUsecase)
    }

    @Test
    fun `When the game is an existing game that has been resumed, the computer players are resumed as well`() {
        // Given
        every { mockInGameStore.gameIsNotNew() } returns true

        // When
        processor.process(ServerEvent.CommunicationEvent.ListeningForConnections)
            .doOnError { error -> Logger.error(error) { "Error" } }
            .test()
            .assertComplete()

        // Then
        verify { mockResumeComputerPlayersUsecase.resumeComputerPlayers() }
        confirmVerified(mockResumeComputerPlayersUsecase)
    }

    private fun launchTest() {
        processor.process(ServerEvent.CommunicationEvent.ListeningForConnections).test().assertComplete()
    }
}
