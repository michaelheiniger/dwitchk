package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import io.mockk.confirmVerified
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
    fun `New game is canceled`() {
        every { mockInGameStore.gameIsNew()} returns true
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        usecase.cancelGame().test().assertComplete()

        testObserver.assertValue(GuestGameEvent.GameCanceled)

        val cancelGameMessageWrapper = EnvelopeToSend(Recipient.All, Message.CancelGameMessage)
        verify { mockCommunicator.sendMessage(cancelGameMessageWrapper) }
        confirmVerified(mockCommunicator)

        verify { mockInGameStore.deleteGame() }
        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Existing game is canceled`() {
        every { mockInGameStore.gameIsNew()} returns false
        val testObserver = gameEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        usecase.cancelGame().test().assertComplete()

        testObserver.assertValue(GuestGameEvent.GameCanceled)

        val cancelGameMessageWrapper = EnvelopeToSend(Recipient.All, Message.CancelGameMessage)
        verify { mockCommunicator.sendMessage(cancelGameMessageWrapper) }
        confirmVerified(mockCommunicator)

        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }
}