package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.GuestMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LeaveGameUsecaseTest : BaseUnitTest() {

    private val playerInGameId = PlayerInGameId(23);

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var leaveGameUsecase: LeaveGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        leaveGameUsecase = LeaveGameUsecase(mockInGameStore, mockServiceManager, mockCommunicator)

        every { mockCommunicator.sendMessage(any()) } returns Completable.complete()
        every { mockInGameStore.getLocalPlayerInGameId() } returns playerInGameId
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `Send leave-game message to host`() {
        leaveGameUsecase.leaveGame().test().assertComplete()
        verify { mockCommunicator.sendMessage(GuestMessageFactory.createLeaveGameMessage(playerInGameId)) }
    }

    @Test
    fun `Close connection with host`() {
        leaveGameUsecase.leaveGame().test().assertComplete()
        verify { mockCommunicator.closeConnection() }
    }

    @Test
    fun `Stop service`() {
        leaveGameUsecase.leaveGame().test().assertComplete()
        verify { mockServiceManager.stopGuestService() }
    }
}