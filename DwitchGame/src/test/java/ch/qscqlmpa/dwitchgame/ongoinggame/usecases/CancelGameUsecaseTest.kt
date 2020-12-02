package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RecipientType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CancelGameUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private lateinit var gameEventRepository: GuestGameEventRepository

    private lateinit var usecase: CancelGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GuestGameEventRepository()
        usecase = CancelGameUsecase(
            mockInGameStore,
            mockCommunicator,
            mockAppEventRepository,
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

        verify { mockAppEventRepository.notify(AppEvent.GameOver) }
    }

    private fun launchTest() {
        usecase.cancelGame().test().assertComplete()
    }

}