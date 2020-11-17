package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LeaveGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.PlayerReadyUsecase
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class WaitingRoomGuestViewModelTest : BaseViewModelUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private val mockPlayerReadyUsecase = mockk<PlayerReadyUsecase>(relaxed = true)

    private val mockLeaveGameUsecase = mockk<LeaveGameUsecase>(relaxed = true)

    private val mockGameEventRepository = mockk<GuestGameEventRepository>(relaxed = true)

    private lateinit var viewModel: WaitingRoomGuestViewModel

    @Before
    override fun setup() {
        super.setup()

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomGuestViewModel(
            mockCommunicator,
            mockPlayerReadyUsecase,
            mockLeaveGameUsecase,
            mockGameEventRepository,
            DisposableManager(),
            schedulerFactory
        )
    }

    @Test
    fun `Publish communication state`() {
        every { mockCommunicator.observeCommunicationState() } returns Observable.just(GuestCommunicationState.Connected)

        val currentCommunicationState = viewModel.currentCommunicationState()
        subscribeToPublishers(currentCommunicationState)

        assertThat(currentCommunicationState.value!!).isEqualTo(GuestCommunicationState.Connected)
        verify { mockCommunicator.observeCommunicationState() }
    }

    @Test
    fun `Publish GameCanceled command when GameCanceled event occurs`() {
        every { mockGameEventRepository.observeEvents() } returns Observable.just(GuestGameEvent.GameCanceled)

        val currentCommunicationState = viewModel.commands()
        subscribeToPublishers(currentCommunicationState)

        assertThat(currentCommunicationState.value).isEqualTo(WaitingRoomGuestCommand.NotifyUserGameCanceled)
        verify { mockGameEventRepository.observeEvents() }
        confirmVerified(mockGameEventRepository)
    }

    @Test
    fun `Publish NavigateToHomeScreen command when user acknowledges GameCanceled event`() {
        every { mockGameEventRepository.observeEvents() } returns Observable.empty()

        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        viewModel.userAcknowledgesGameCanceledEvent()

        assertThat(commands.value).isEqualTo(WaitingRoomGuestCommand.NavigateToHomeScreen)
    }
}