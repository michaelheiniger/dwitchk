package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.GuestMessageFactory
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GuestLeavesGameUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)
    private val mockGuestGameLifecycleEventRepository = mockk<GuestGameLifecycleEventRepository>(relaxed = true)

    private lateinit var guestLeavesGameUsecase: GuestLeavesGameUsecase

    private val playerDwitchId = DwitchPlayerId(23)

    @BeforeEach
    fun setup() {
        guestLeavesGameUsecase = GuestLeavesGameUsecase(mockInGameStore, mockGuestGameLifecycleEventRepository, mockCommunicator)

        every { mockInGameStore.getLocalPlayerDwitchId() } returns playerDwitchId
    }

    @Test
    fun `Local player (guest) is leaving the new game`() {
        every { mockInGameStore.gameIsNew() } returns true

        launchTest()

        verify { mockCommunicator.sendMessageToHost(GuestMessageFactory.createLeaveGameMessage(playerDwitchId)) }
        confirmVerified(mockCommunicator)

        verify { mockInGameStore.gameIsNew() }
        verify { mockInGameStore.getLocalPlayerDwitchId() }
        verify { mockInGameStore.markGameForDeletion() }
        confirmVerified(mockInGameStore)

        verify { mockGuestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver) }
    }

    @Test
    fun `Local player (guest) is leaving the existing game`() {
        every { mockInGameStore.gameIsNew() } returns false

        launchTest()

        confirmVerified(mockCommunicator)

        verify { mockInGameStore.gameIsNew() }
        confirmVerified(mockInGameStore)

        verify { mockGuestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver) }
    }

    private fun launchTest() {
        guestLeavesGameUsecase.leaveGame().test().assertComplete()
    }
}
