package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EndGameUsecaseTest : BaseUnitTest() {

    private lateinit var gameEventRepository: GuestGameEventRepository

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private val mockCommunicator = mockk<GameCommunicator>(relaxed = true)

    private lateinit var usecase: EndGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GuestGameEventRepository()
        usecase = EndGameUsecase(
            gameEventRepository,
            mockServiceManager,
            mockCommunicator
        )
        every { mockCommunicator.sendMessage((any())) } returns Completable.complete()
    }

    @Test
    fun `Broadcast GameOver message`() {
        launchTest()

        verify { mockCommunicator.sendMessage(EnvelopeToSend(RecipientType.All, Message.GameOverMessage)) }
    }

    @Test
    fun `Stop service`() {
        launchTest()

        verify { mockServiceManager.stopHostService() }
    }

    @Test
    fun `Notify GameOver game event`() {
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(GuestGameEvent.GameOver)
    }

    private fun launchTest() {
        usecase.endGame().test().assertComplete()
    }
}