package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EndGameUsecaseTest : BaseUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private lateinit var usecase: EndGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        usecase = EndGameUsecase(
            mockAppEventRepository,
            mockCommunicator
        )
        every { mockCommunicator.sendMessage((any())) } returns Completable.complete()
    }

    @Test
    fun `Broadcast GameOver message`() {
        launchTest()

        verify { mockCommunicator.sendMessage(EnvelopeToSend(Recipient.AllGuests, Message.GameOverMessage)) }
    }

    @Test
    fun `Stop service`() {
        launchTest()

        verify { mockAppEventRepository.notify(AppEvent.GameOver) }
    }

    private fun launchTest() {
        usecase.endGame().test().assertComplete()
    }
}