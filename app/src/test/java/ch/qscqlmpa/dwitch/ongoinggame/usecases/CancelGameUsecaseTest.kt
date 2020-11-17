package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
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

class CancelGameUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private lateinit var gameEventRepository: GuestGameEventRepository

    private lateinit var usecase: CancelGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GuestGameEventRepository()
        usecase = CancelGameUsecase(
            mockInGameStore,
            mockCommunicator,
            mockServiceManager,
            gameEventRepository
        )

        every { mockCommunicator.sendMessage(any()) } returns Completable.complete()
    }

    @Test
    fun `Game is deleted from Store`() {
        launchTest()

        verify { mockInGameStore.deleteGame() }
    }

    @Test
    fun `"Cancel game" message is sent`() {
        launchTest()

        val cancelGameMessageWrapper = EnvelopeToSend(RecipientType.All, Message.CancelGameMessage)

        verify { mockCommunicator.sendMessage(cancelGameMessageWrapper) }
    }

    @Test
    fun `GameCanceled event is emitted`() {
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(GuestGameEvent.GameCanceled)
    }

    @Test
    fun `Connections are closed and service is stopped`() {
        launchTest()

        verify { mockServiceManager.stopHostService() }
    }

    private fun launchTest() {
        usecase.cancelGame().test().assertComplete()
    }

}