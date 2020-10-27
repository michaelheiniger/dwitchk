package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EndGameUsecaseTest : BaseUnitTest() {

    private lateinit var gameEventRepository: GameEventRepository

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private val mockCommunicator = mockk<GameCommunicator>(relaxed = true)

    private lateinit var endGameUsecase: EndGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GameEventRepository()
        endGameUsecase = EndGameUsecase(
            gameEventRepository,
            mockServiceManager,
            mockCommunicator
        )
        every { mockCommunicator.sendMessage((any())) } returns Completable.complete()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockCommunicator, mockServiceManager)
    }

    @Test
    fun `Broadcast GameOver message`() {
        endGameUsecase.endGame().test().assertComplete()

        verify { mockCommunicator.sendMessage(EnvelopeToSend(RecipientType.All, Message.GameOverMessage)) }
    }

    @Test
    fun `Stop service`() {
        endGameUsecase.endGame().test().assertComplete()

        verify { mockServiceManager.stopHostService() }
    }

    @Test
    fun `Notify GameOver game event`() {
        assertThat(gameEventRepository.getLastEvent()).isNull()

        endGameUsecase.endGame().test().assertComplete()

        assertThat(gameEventRepository.getLastEvent()).isEqualTo(GameEvent.GameOver)
    }
}