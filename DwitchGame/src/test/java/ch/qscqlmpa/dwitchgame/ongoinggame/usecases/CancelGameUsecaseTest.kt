package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
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
        testObserver.assertValue(HostGameLifecycleEvent.GameCanceled)

        // And guests are notified
        val cancelGameMessageWrapper = EnvelopeToSend(Recipient.All, Message.CancelGameMessage)
        verify { mockCommunicator.sendMessage(cancelGameMessageWrapper) }
        confirmVerified(mockCommunicator)

        // And game is deleted
        verify { mockInGameStore.deleteGame() }
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
        testObserver.assertValue(HostGameLifecycleEvent.GameCanceled)

        // And guests are notified
        val cancelGameMessageWrapper = EnvelopeToSend(Recipient.All, Message.CancelGameMessage)
        verify { mockCommunicator.sendMessage(cancelGameMessageWrapper) }
        confirmVerified(mockCommunicator)

        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }
}
