package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameUpdatedUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<GameCommunicator>()

    private lateinit var usecase: GameUpdatedUsecase

    @BeforeEach
    override fun setup() {
        super.setup()

        usecase = GameUpdatedUsecase(mockInGameStore, mockCommunicator)

        every { mockCommunicator.sendGameState(any()) } returns Completable.complete()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockCommunicator)
    }

    @Test
    fun `should store up-to-date game state`() {

        val gameInfo = TestEntityFactory.createGameInfo()

        usecase.handleUpdatedGameState(gameInfo).test().assertComplete()

        verify { mockInGameStore.updateGameState(gameInfo.gameState) }
    }

    @Test
    fun `should send up-to-date game state to all`() {

        val gameInfo = TestEntityFactory.createGameInfo()

        usecase.handleUpdatedGameState(gameInfo).test().assertComplete()

        val messageWrapperRef = EnvelopeToSend(RecipientType.All, Message.GameStateUpdatedMessage(gameInfo.gameState))
        verify { mockCommunicator.sendGameState(messageWrapperRef) }
    }
}