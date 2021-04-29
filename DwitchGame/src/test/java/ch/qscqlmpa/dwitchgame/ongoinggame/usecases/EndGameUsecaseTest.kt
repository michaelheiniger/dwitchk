package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EndGameUsecaseTest : BaseUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private lateinit var usecase: EndGameUsecase

    @BeforeEach
    fun setup() {
        usecase = EndGameUsecase(
            mockAppEventRepository,
            mockCommunicator
        )
    }

    @Test
    fun `Broadcast GameOver message`() {
        launchTest()

        verify { mockCommunicator.sendMessage(EnvelopeToSend(Recipient.All, Message.GameOverMessage)) }
    }

    @Test
    fun `Stop service`() {
        launchTest()

        verify { mockAppEventRepository.notify(AppEvent.GameOverHost) }
    }

    private fun launchTest() {
        usecase.endGame().test().assertComplete()
    }
}
