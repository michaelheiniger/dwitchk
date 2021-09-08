package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.services.GameInitializerService
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LaunchGameUsecaseTest : BaseUnitTest() {

    private val mockGameInitializerService = mockk<GameInitializerService>(relaxed = true)
    private val mockHostGameLifecycleEventRepository = mockk<HostGameLifecycleEventRepository>(relaxed = true)
    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private lateinit var launchGameUsecase: LaunchGameUsecase

    private val mockNewGameState = mockk<DwitchGameState>()
    private val mockExistingGameState = mockk<DwitchGameState>()

    @BeforeEach
    fun setup() {
        launchGameUsecase = LaunchGameUsecase(
            mockInGameStore,
            mockGameInitializerService,
            mockHostGameLifecycleEventRepository,
            mockCommunicator
        )

        every { mockGameInitializerService.initializeGameState() } returns mockNewGameState
        every { mockInGameStore.getGameState() } returns mockExistingGameState
    }

    @Test
    fun `Launch new game`() {
        // Given the game is a new one
        every { mockInGameStore.gameIsNew() } returns true

        // When launching the game
        launchTest()

        // Then
        val envelopeCap = CapturingSlot<EnvelopeToSend>()
        verify { mockCommunicator.sendMessage(capture(envelopeCap)) }
        val messageSent = envelopeCap.captured.message as Message.LaunchGameMessage
        assertThat(messageSent.gameState).isEqualTo(mockNewGameState)
        confirmVerified(mockCommunicator)

        verify { mockHostGameLifecycleEventRepository.notify(HostGameLifecycleEvent.MovedToGameRoom) }
    }

    @Test
    fun `Launch (resume) existing game`() {
        // Given the game is a resumed one
        every { mockInGameStore.gameIsNew() } returns false

        // When launching the game
        launchTest()

        // Then
        val envelopeCap = CapturingSlot<EnvelopeToSend>()
        verify { mockCommunicator.sendMessage(capture(envelopeCap)) }
        val messageSent = envelopeCap.captured.message as Message.LaunchGameMessage
        assertThat(messageSent.gameState).isEqualTo(mockExistingGameState)
        confirmVerified(mockCommunicator)

        verify { mockHostGameLifecycleEventRepository.notify(HostGameLifecycleEvent.MovedToGameRoom) }
    }

    private fun launchTest() {
        launchGameUsecase.launchGame().test().assertComplete()
    }
}
