package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchcommunication.ingame.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.model.Recipient
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CancelGameUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)
    private val mockGameLifecycleEventRepository = mockk<HostGameLifecycleEventRepository>(relaxed = true)

    private lateinit var usecase: CancelGameUsecase

    @BeforeEach
    fun setup() {
        usecase = CancelGameUsecase(
            mockInGameStore,
            mockCommunicator,
            mockGameLifecycleEventRepository,
        )
    }

    @Test
    fun `New game is canceled`() {
        // Given the game is a new one
        every { mockInGameStore.gameIsNew() } returns true
        val testObserver = mockGameLifecycleEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        // When the host cancels the game
        usecase.cancelGame().test().assertComplete()

        // Then game canceled event is emitted
        testObserver.assertValue(HostGameLifecycleEvent.GameOver)

        // And guests are notified
        val cancelGameMessageWrapper = EnvelopeToSend(Recipient.All, Message.CancelGameMessage)
        verify { mockCommunicator.sendMessage(cancelGameMessageWrapper) }
        confirmVerified(mockCommunicator)

        // And game is deleted
        verify { mockInGameStore.markGameForDeletion() }
        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Existing game is canceled`() {
        // Given the game is a resumed one
        every { mockInGameStore.gameIsNew() } returns false
        val testObserver = mockGameLifecycleEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        // When the host cancels the game
        usecase.cancelGame().test().assertComplete()

        // Then game canceled event is emitted
        testObserver.assertValue(HostGameLifecycleEvent.GameOver)

        // And guests are notified
        val cancelGameMessageWrapper = EnvelopeToSend(Recipient.All, Message.CancelGameMessage)
        verify { mockCommunicator.sendMessage(cancelGameMessageWrapper) }
        confirmVerified(mockCommunicator)

        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }
}
