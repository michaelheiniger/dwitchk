package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
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

class CancelGameUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private lateinit var gameEventRepository: GameEventRepository

    private lateinit var usecase: CancelGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GameEventRepository()
        usecase = CancelGameUsecase(
            mockInGameStore,
            mockCommunicator,
            mockServiceManager,
            gameEventRepository
        )

        every { mockCommunicator.sendMessage(any()) } returns Completable.complete()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockCommunicator, mockServiceManager)
    }

    @Test
    fun `Game is deleted from Store`() {
        usecase.cancelGame().test().assertComplete()

        verify { mockInGameStore.deleteGame() }
    }

    @Test
    fun `"Cancel game" message is sent`() {
        usecase.cancelGame().test().assertComplete()

        val cancelGameMessageWrapper = EnvelopeToSend(RecipientType.All, Message.CancelGameMessage)

        verify { mockCommunicator.sendMessage(cancelGameMessageWrapper) }
    }

    @Test
    fun `GameCanceled event is emitted`() {
        assertThat(gameEventRepository.getLastEvent()).isNull()

        usecase.cancelGame().test().assertComplete()

        assertThat(gameEventRepository.getLastEvent()).isEqualTo(GameEvent.GameCanceled)
    }

    @Test
    fun `Connections are closed and service is stopped`() {
        usecase.cancelGame().test().assertComplete()

        verify { mockServiceManager.stopHostService() }
    }
}