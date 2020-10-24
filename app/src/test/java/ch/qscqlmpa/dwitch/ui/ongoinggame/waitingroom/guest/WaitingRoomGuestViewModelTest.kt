package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.PlayerReadyUsecase
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class WaitingRoomGuestViewModelTest : BaseViewModelUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private val mockPlayerReadyUsecase = mockk<PlayerReadyUsecase>(relaxed = true)

    private val mockGameEventRepository = mockk<GameEventRepository>(relaxed = true)

    private lateinit var viewModel: WaitingRoomGuestViewModel

    @Before
    override fun setup() {
        super.setup()

        every { mockCommunicator.connect() } just Runs

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomGuestViewModel(
            mockCommunicator,
            mockPlayerReadyUsecase,
            mockGameEventRepository,
            DisposableManager(),
            schedulerFactory
        )
    }

    @After
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockCommunicator, mockPlayerReadyUsecase, mockGameEventRepository)
    }

    @Test
    fun `Connect to host`() {
        verify { mockCommunicator.connect() }
        confirmVerified(mockCommunicator)
    }

    @Test
    fun `Publish communication state`() {

        every { mockCommunicator.observeCommunicationState() } returns Observable.just(
            GuestCommunicationState.CONNECTED
        )

        val currentCommunicationState = viewModel.currentCommunicationState()
        subscribeToPublishers(currentCommunicationState)

        assertThat(currentCommunicationState.value!!.id).isEqualTo(R.string.connected_to_host)
        verify { mockCommunicator.observeCommunicationState() }
    }

    @Test
    fun `Publish NotifyUserGameCanceled command when GameCanceled event occurs`() {
        every { mockGameEventRepository.observeEvents() } returns Observable.just(GameEvent.GameCanceled)

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