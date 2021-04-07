package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.services.ChangeCurrentRoomService
import ch.qscqlmpa.dwitchgame.ongoinggame.services.GameInitializerService
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LaunchGameUsecaseTest : BaseUnitTest() {

    private val mockChangeCurrentRoomService = mockk<ChangeCurrentRoomService>(relaxed = true)

    private val mockGameInitializerService = mockk<GameInitializerService>(relaxed = true)

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private lateinit var launchGameUsecase: LaunchGameUsecase

    private val mockNewGameState = mockk<DwitchGameState>()
    private val mockExistingGameState = mockk<DwitchGameState>()

    @BeforeEach
    override fun setup() {
        super.setup()
        launchGameUsecase = LaunchGameUsecase(
            mockInGameStore,
            mockGameInitializerService,
            mockChangeCurrentRoomService,
            mockCommunicator
        )

        every { mockGameInitializerService.initializeGameState() } returns mockNewGameState
        every { mockInGameStore.getGameState() } returns mockExistingGameState
    }

    @Test
    fun `Launch new game`() {
        every { mockInGameStore.gameIsNew() } returns true

        launchTest()

        val envelopeCap = CapturingSlot<EnvelopeToSend>()
        verify { mockCommunicator.sendMessage(capture(envelopeCap)) }
        val messageSent = envelopeCap.captured.message as Message.LaunchGameMessage
        assertThat(messageSent.gameState).isEqualTo(mockNewGameState)
        confirmVerified(mockCommunicator)

        verify { mockChangeCurrentRoomService.moveToGameRoom() }
    }

    @Test
    fun `Launch (resume) existing game`() {
        every { mockInGameStore.gameIsNew() } returns false

        launchTest()

        val envelopeCap = CapturingSlot<EnvelopeToSend>()
        verify { mockCommunicator.sendMessage(capture(envelopeCap)) }
        val messageSent = envelopeCap.captured.message as Message.LaunchGameMessage
        assertThat(messageSent.gameState).isEqualTo(mockExistingGameState)
        confirmVerified(mockCommunicator)

        verify { mockChangeCurrentRoomService.moveToGameRoom() }
    }

    private fun launchTest() {
        launchGameUsecase.launchGame().test().assertComplete()
    }
}
