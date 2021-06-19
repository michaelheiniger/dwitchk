package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.GuestMessageFactory
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GuestLeavesGameUsecaseTest : BaseUnitTest() {

    private lateinit var gameLifecycleEventRepository: GuestGameLifecycleEventRepository
    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var guestLeavesGameUsecase: GuestLeavesGameUsecase

    private val playerDwitchId = DwitchPlayerId(23)

    @BeforeEach
    fun setup() {
        gameLifecycleEventRepository = GuestGameLifecycleEventRepository()
        guestLeavesGameUsecase = GuestLeavesGameUsecase(mockInGameStore, gameLifecycleEventRepository, mockCommunicator)

        every { mockInGameStore.getLocalPlayerDwitchId() } returns playerDwitchId
    }

    @Test
    fun `Local player (guest) is leaving the new game`() {
        every { mockInGameStore.gameIsNew() } returns true
        val testObserver = gameLifecycleEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(GuestGameLifecycleEvent.GuestLeftGame)

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
        val testObserver = gameLifecycleEventRepository.observeEvents().test()
        testObserver.assertNoValues()

        launchTest()

        testObserver.assertValue(GuestGameLifecycleEvent.GuestLeftGame)

        confirmVerified(mockCommunicator)

        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)
    }

    private fun launchTest() {
        guestLeavesGameUsecase.leaveGame().test().assertComplete()
    }
}
