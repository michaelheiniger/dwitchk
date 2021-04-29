package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameUpdatedUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<GameCommunicator>(relaxed = true)

    private lateinit var usecase: GameUpdatedUsecase

    @BeforeEach
    fun setup() {
        usecase = GameUpdatedUsecase(mockInGameStore, mockCommunicator)
    }

    @Test
    fun `should store up-to-date game state`() {
        val gameState = TestEntityFactory.createGameState()

        usecase.handleUpdatedGameState(gameState).test().assertComplete()

        verify { mockInGameStore.updateGameState(gameState) }
    }

    @Test
    fun `should send up-to-date game state to host`() {
        val gameState = TestEntityFactory.createGameState()

        usecase.handleUpdatedGameState(gameState).test().assertComplete()

        verify { mockCommunicator.sendMessageToHost(Message.GameStateUpdatedMessage(gameState)) }
    }
}
