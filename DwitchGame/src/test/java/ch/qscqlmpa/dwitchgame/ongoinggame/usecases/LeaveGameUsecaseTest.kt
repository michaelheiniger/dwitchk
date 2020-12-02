package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LeaveGameUsecaseTest : BaseUnitTest() {

    private val playerInGameId = PlayerInGameId(23);

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var leaveGameUsecase: LeaveGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        leaveGameUsecase = LeaveGameUsecase(mockInGameStore, mockAppEventRepository, mockCommunicator)

        every { mockCommunicator.sendMessage(any()) } returns Completable.complete()
        every { mockInGameStore.getLocalPlayerInGameId() } returns playerInGameId
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
        verify { mockAppEventRepository.notify(AppEvent.GameLeft) }
    }
}