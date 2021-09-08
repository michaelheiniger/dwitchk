package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EndGameUsecaseTest : BaseUnitTest() {

    private val mockGameLifecycleEventRepository = mockk<HostGameLifecycleEventRepository>(relaxed = true)

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private lateinit var usecase: EndGameUsecase

    @BeforeEach
    fun setup() {
        usecase = EndGameUsecase(
            mockGameLifecycleEventRepository,
            mockCommunicator
        )
    }

    @Test
    fun `Send GameOver message to all guests`() {
        launchTest()

        verify { mockCommunicator.sendMessage(EnvelopeToSend(Recipient.All, Message.GameOverMessage)) }
    }

    @Test
    fun `Stop service`() {
        launchTest()

        verify { mockGameLifecycleEventRepository.notify(HostGameLifecycleEvent.GameOver) }
    }

    private fun launchTest() {
        usecase.endGame().test().assertComplete()
    }
}
