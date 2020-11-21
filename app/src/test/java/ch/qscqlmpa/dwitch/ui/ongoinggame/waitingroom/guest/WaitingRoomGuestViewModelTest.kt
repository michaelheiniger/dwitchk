package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomGuestFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class WaitingRoomGuestViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomGuestFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomGuestViewModel

    private lateinit var communicationStateSubject: PublishSubject<GuestCommunicationState>

    private lateinit var localPlayerReadyStateSubject: PublishSubject<Boolean>

    @Before
    override fun setup() {
        super.setup()

        viewModel = WaitingRoomGuestViewModel(mockFacade, ch.qscqlmpa.dwitchcommonutil.DisposableManager(), TestSchedulerFactory())

        communicationStateSubject = PublishSubject.create()
        localPlayerReadyStateSubject = PublishSubject.create()

        every { mockFacade.observeCommunicationState() } returns communicationStateSubject
        every { mockFacade.observeLocalPlayerReadyState() } returns localPlayerReadyStateSubject
        every { mockFacade.leaveGame() } returns Completable.complete()
    }

    @Test
    fun `Local player ready state control is enabled and checked when communication state is Connected and player is ready`() {
        val localPlayerReadyStateInfo = viewModel.localPlayerReadyStateInfo()
        subscribeToPublishers(localPlayerReadyStateInfo)

        communicationStateSubject.onNext(GuestCommunicationState.Connected)
        localPlayerReadyStateSubject.onNext(true)

        assertThat(localPlayerReadyStateInfo.value!!).isEqualTo(UiCheckboxModel(enabled = true, checked = true))
    }

    @Test
    fun `Local player ready state control is enabled and unchecked when communication state is Connected and player is not ready`() {
        val localPlayerReadyStateInfo = viewModel.localPlayerReadyStateInfo()
        subscribeToPublishers(localPlayerReadyStateInfo)

        communicationStateSubject.onNext(GuestCommunicationState.Connected)
        localPlayerReadyStateSubject.onNext(false)

        assertThat(localPlayerReadyStateInfo.value!!).isEqualTo(UiCheckboxModel(enabled = true, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Disconnected`() {
        val localPlayerReadyStateInfo = viewModel.localPlayerReadyStateInfo()
        subscribeToPublishers(localPlayerReadyStateInfo)

        communicationStateSubject.onNext(GuestCommunicationState.Disconnected)
        localPlayerReadyStateSubject.onNext(true)

        assertThat(localPlayerReadyStateInfo.value!!).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Error`() {
        val localPlayerReadyStateInfo = viewModel.localPlayerReadyStateInfo()
        subscribeToPublishers(localPlayerReadyStateInfo)

        communicationStateSubject.onNext(GuestCommunicationState.Error)
        localPlayerReadyStateSubject.onNext(true)

        assertThat(localPlayerReadyStateInfo.value!!).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Update ready state action should forward call with true`() {
        viewModel.updateReadyState(true)
        verify { mockFacade.updateReadyState(true) }
    }

    @Test
    fun `Update ready state action should forward call with false`() {
        viewModel.updateReadyState(false)
        verify { mockFacade.updateReadyState(false) }
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
        verify { mockFacade.leaveGame() }
    }

    @Test
    fun `Publish GameCanceled command when GameCanceled event occurs`() {
        every { mockFacade.observeEvents() } returns Observable.just(GuestGameEvent.GameCanceled)

        val currentCommunicationState = viewModel.commands()
        subscribeToPublishers(currentCommunicationState)

        assertThat(currentCommunicationState.value).isEqualTo(WaitingRoomGuestCommand.NotifyUserGameCanceled)
        verify { mockFacade.observeEvents() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Publish NavigateToHomeScreen command when user acknowledges GameCanceled event`() {
        every { mockFacade.observeEvents() } returns Observable.empty()

        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        viewModel.acknowledgeGameCanceledEvent()

        assertThat(commands.value).isEqualTo(WaitingRoomGuestCommand.NavigateToHomeScreen)
    }
}