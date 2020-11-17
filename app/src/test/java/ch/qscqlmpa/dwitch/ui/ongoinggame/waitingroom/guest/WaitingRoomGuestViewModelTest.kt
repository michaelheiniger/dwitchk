package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWr
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.WaitingRoomPlayerRepository
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LeaveGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.PlayerReadyUsecase
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.ui.model.CheckboxModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.model.UiInfoModel
import ch.qscqlmpa.dwitch.ui.model.Visibility
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class WaitingRoomGuestViewModelTest : BaseViewModelUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private val mockPlayerReadyUsecase = mockk<PlayerReadyUsecase>(relaxed = true)

    private val mockLeaveGameUsecase = mockk<LeaveGameUsecase>(relaxed = true)

    private val mockWrPlayerRepository = mockk<WaitingRoomPlayerRepository>(relaxed = true)

    private val mockGameEventRepository = mockk<GuestGameEventRepository>(relaxed = true)

    private lateinit var viewModel: WaitingRoomGuestViewModel

    private lateinit var communicatorSubject: PublishSubject<GuestCommunicationState>;

    private lateinit var wrPlayerRepositorySubject: PublishSubject<PlayerWr>

    @Before
    override fun setup() {
        super.setup()

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomGuestViewModel(
            mockCommunicator,
            mockPlayerReadyUsecase,
            mockLeaveGameUsecase,
            mockWrPlayerRepository,
            mockGameEventRepository,
            DisposableManager(),
            schedulerFactory
        )

        communicatorSubject = PublishSubject.create()
        wrPlayerRepositorySubject = PublishSubject.create()

        every { mockCommunicator.observeCommunicationState() } returns communicatorSubject
        every { mockWrPlayerRepository.observeLocalPlayer() } returns wrPlayerRepositorySubject
        every { mockLeaveGameUsecase.leaveGame() } returns Completable.complete()
    }

    @Test
    fun `Local player ready state control is enabled and checked when communication state is Connected and player is ready`() {
        val localPlayerReadyStateInfo = viewModel.localPlayerReadyStateInfo()
        subscribeToPublishers(localPlayerReadyStateInfo)

        communicatorSubject.onNext(GuestCommunicationState.Connected)
        wrPlayerRepositorySubject.onNext(PlayerWr(TestEntityFactory.createGuestPlayer1(ready = true)))

        assertThat(localPlayerReadyStateInfo.value!!).isEqualTo(CheckboxModel(enabled = true, checked = true))
    }

    @Test
    fun `Local player ready state control is enabled and unchecked when communication state is Connected and player is not ready`() {
        val localPlayerReadyStateInfo = viewModel.localPlayerReadyStateInfo()
        subscribeToPublishers(localPlayerReadyStateInfo)

        communicatorSubject.onNext(GuestCommunicationState.Connected)
        wrPlayerRepositorySubject.onNext(PlayerWr(TestEntityFactory.createGuestPlayer1(ready = false)))

        assertThat(localPlayerReadyStateInfo.value!!).isEqualTo(CheckboxModel(enabled = true, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Disconnected`() {
        val localPlayerReadyStateInfo = viewModel.localPlayerReadyStateInfo()
        subscribeToPublishers(localPlayerReadyStateInfo)

        communicatorSubject.onNext(GuestCommunicationState.Disconnected)
        wrPlayerRepositorySubject.onNext(PlayerWr(TestEntityFactory.createGuestPlayer1(ready = true)))

        assertThat(localPlayerReadyStateInfo.value!!).isEqualTo(CheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Error`() {
        val localPlayerReadyStateInfo = viewModel.localPlayerReadyStateInfo()
        subscribeToPublishers(localPlayerReadyStateInfo)

        communicatorSubject.onNext(GuestCommunicationState.Error)
        wrPlayerRepositorySubject.onNext(PlayerWr(TestEntityFactory.createGuestPlayer1(ready = true)))

        assertThat(localPlayerReadyStateInfo.value!!).isEqualTo(CheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Reconnect action control is not displayed when communication state is Connected`() {
        val reconnectAction = viewModel.reconnectAction()
        subscribeToPublishers(reconnectAction)

        communicatorSubject.onNext(GuestCommunicationState.Connected)

        assertThat(reconnectAction.value!!).isEqualToIgnoringGivenFields(UiControlModel(visibility = Visibility.Gone), "enabled")
    }

    @Test
    fun `Reconnect action control is displayed when communication state is Disconnected`() {
        val reconnectAction = viewModel.reconnectAction()
        subscribeToPublishers(reconnectAction)

        communicatorSubject.onNext(GuestCommunicationState.Disconnected)

        assertThat(reconnectAction.value!!).isEqualToIgnoringGivenFields(UiControlModel(visibility = Visibility.Visible), "enabled")
    }

    @Test
    fun `Reconnect action control is displayed when communication state is Error`() {
        val reconnectAction = viewModel.reconnectAction()
        subscribeToPublishers(reconnectAction)

        communicatorSubject.onNext(GuestCommunicationState.Error)

        assertThat(reconnectAction.value!!).isEqualToIgnoringGivenFields(UiControlModel(visibility = Visibility.Visible), "enabled")
    }

    @Test
    fun `Reconnect action control is disabled when a reconnect action is performed`() {
        val reconnectAction = viewModel.reconnectAction()
        subscribeToPublishers(reconnectAction)

        viewModel.reconnect()

        assertThat(reconnectAction.value!!).isEqualTo(UiControlModel(enabled = false, visibility = Visibility.Visible))
    }

    @Test
    fun `Reconnect loading control is hidden when communication state changes to Connected`() {
        reconnectLoadingControlIsHiddenWHenCommunicationStateChanges(GuestCommunicationState.Connected)
    }

    @Test
    fun `Reconnect loading control is hidden when communication state changes to Disconnected`() {
        reconnectLoadingControlIsHiddenWHenCommunicationStateChanges(GuestCommunicationState.Disconnected)
    }

    @Test
    fun `Reconnect loading control is hidden when communication state changes to Error`() {
        reconnectLoadingControlIsHiddenWHenCommunicationStateChanges(GuestCommunicationState.Error)
    }

    @Test
    fun `Reconnect loading control is hidden when a reconnect action is performed`() {
        val reconnectLoading = viewModel.reconnectLoading()
        subscribeToPublishers(reconnectLoading)

        viewModel.reconnect()

        assertThat(reconnectLoading.value!!).isEqualToIgnoringGivenFields(UiControlModel(visibility = Visibility.Visible), "enabled")
    }

    @Test
    fun `Connection state info is updated whenever the connection state changes`() {
        val connectionStateInfo = viewModel.connectionStateInfo()
        subscribeToPublishers(connectionStateInfo)

        communicatorSubject.onNext(GuestCommunicationState.Connected)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(GuestCommunicationState.Connected.resourceId))

        communicatorSubject.onNext(GuestCommunicationState.Disconnected)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(GuestCommunicationState.Disconnected.resourceId))

        communicatorSubject.onNext(GuestCommunicationState.Error)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(GuestCommunicationState.Error.resourceId))
    }

    @Test
    fun `Reconnect action should forward call`() {
        viewModel.reconnect()
        verify { mockCommunicator.connect() }
    }

    @Test
    fun `Update ready state action should forward call with true`() {
        viewModel.updateReadyState(true)
        verify { mockPlayerReadyUsecase.updateReadyState(true) }
    }

    @Test
    fun `Update ready state action should forward call with false`() {
        viewModel.updateReadyState(false)
        verify { mockPlayerReadyUsecase.updateReadyState(false) }
    }

    @Test
    fun `Publish command to navigate to home screen when user acknowledges that the game has been canceled`() {
        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        viewModel.acknowledgeGameCanceledEvent()

        assertThat(commands.value!!).isEqualTo(WaitingRoomGuestCommand.NavigateToHomeScreen)
    }

    @Test
    fun `Leave game action should forward call and emit command to navigate to home screen when completed`() {
        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        viewModel.leaveGame()

        assertThat(commands.value!!).isEqualTo(WaitingRoomGuestCommand.NavigateToHomeScreen)
        verify { mockLeaveGameUsecase.leaveGame() }
    }


    private fun reconnectLoadingControlIsHiddenWHenCommunicationStateChanges(state: GuestCommunicationState) {
        val reconnectLoading = viewModel.reconnectLoading()
        subscribeToPublishers(reconnectLoading)

        viewModel.reconnect()

        assertThat(reconnectLoading.value!!).isEqualToIgnoringGivenFields(UiControlModel(visibility = Visibility.Visible), "enabled")

        communicatorSubject.onNext(state) // Change in communication state

        assertThat(reconnectLoading.value!!).isEqualToIgnoringGivenFields(UiControlModel(visibility = Visibility.Gone), "enabled")
    }


    ////

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

        viewModel.acknowledgeGameCanceledEvent()

        assertThat(commands.value).isEqualTo(WaitingRoomGuestCommand.NavigateToHomeScreen)
    }
}