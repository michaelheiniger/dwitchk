package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RecipientType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EndGameUsecaseTest : BaseUnitTest() {

    private lateinit var gameEventRepository: GuestGameEventRepository

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private val mockCommunicator = mockk<GameCommunicator>(relaxed = true)

    private lateinit var usecase: EndGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        gameEventRepository = GuestGameEventRepository()
        usecase = EndGameUsecase(
            gameEventRepository,
            mockAppEventRepository,
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

        verify { mockAppEventRepository.notify(AppEvent.GameOver) }
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