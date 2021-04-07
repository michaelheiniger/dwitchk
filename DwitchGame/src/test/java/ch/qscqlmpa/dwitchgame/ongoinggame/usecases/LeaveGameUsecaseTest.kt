package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LeaveGameUsecaseTest : BaseUnitTest() {

    private val playerDwitchId = DwitchPlayerId(23)

    private lateinit var appEventRepository: AppEventRepository

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var leaveGameUsecase: LeaveGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        appEventRepository = AppEventRepository()
        leaveGameUsecase = LeaveGameUsecase(mockInGameStore, appEventRepository, mockCommunicator)

        every { mockInGameStore.getLocalPlayerDwitchId() } returns playerDwitchId
    }

    @Test
    fun `Local player (guest) is leaving the new game`() {
        every { mockInGameStore.gameIsNew() } returns true
        val testObserver = appEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(AppEvent.GameLeft)

        verify { mockCommunicator.sendMessageToHost(GuestMessageFactory.createLeaveGameMessage(playerDwitchId)) }
        confirmVerified(mockCommunicator)

        verify { mockInGameStore.gameIsNew() }
        verify { mockInGameStore.getLocalPlayerDwitchId() }
        verify { mockInGameStore.deleteGame() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Local player (guest) is leaving the existing game`() {
        every { mockInGameStore.gameIsNew() } returns false
        val testObserver = appEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(AppEvent.GameLeft)

        confirmVerified(mockCommunicator)

        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }

    private fun launchTest() {
        leaveGameUsecase.leaveGame().test().assertComplete()
    }
}
