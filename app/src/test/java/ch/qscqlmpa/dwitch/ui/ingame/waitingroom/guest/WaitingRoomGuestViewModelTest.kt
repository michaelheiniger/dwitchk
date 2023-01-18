package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest

import ch.qscqlmpa.dwitch.app.StubIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination.Home
import ch.qscqlmpa.dwitch.ui.navigation.InGameGuestDestination
import ch.qscqlmpa.dwitch.ui.navigation.ScreenNavigator
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomGuestFacade
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WaitingRoomGuestViewModelTest : BaseViewModelUnitTest() {

    private val mockWaitingRoomGuestFacade = mockk<WaitingRoomGuestFacade>(relaxed = true)
    private val communicationFacade = mockk<GuestCommunicationFacade>(relaxed = true)
    private val inGameGuestFacade = mockk<InGameGuestFacade>(relaxed = true)
    private val mockNavigationBridge = mockk<ScreenNavigator>(relaxed = true)

    private lateinit var viewModel: WaitingRoomGuestViewModel

    private lateinit var communicationStateSubject: PublishSubject<GuestCommunicationState>
    private lateinit var localPlayerReadyStateSubject: PublishSubject<Boolean>
    private lateinit var gameEventSubject: PublishSubject<GuestGameEvent>

    @Test
    fun `Local player ready control is initially disabled and unchecked`() {
        // Given initial values (nothing to setup)

        // When
        createViewModel()
        viewModel.onStart()

        // Then
        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Local player ready control is enabled and checked when communication state is Connected and player is ready`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        communicationStateSubject.onNext(GuestCommunicationState.Connected)
        localPlayerReadyStateSubject.onNext(true)

        // Then
        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = true, checked = true))
    }

    @Test
    fun `Local player ready state control is enabled and unchecked when communication state is Connected and player is not ready`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        communicationStateSubject.onNext(GuestCommunicationState.Connected)
        localPlayerReadyStateSubject.onNext(false)

        // Then
        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = true, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Connecting`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        communicationStateSubject.onNext(GuestCommunicationState.Connecting)
        localPlayerReadyStateSubject.onNext(true)

        // Then
        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Disconnected`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        communicationStateSubject.onNext(GuestCommunicationState.Disconnected(connectedToWlan = true))
        localPlayerReadyStateSubject.onNext(true)

        // Then
        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Error`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        communicationStateSubject.onNext(GuestCommunicationState.Error(connectedToWlan = true))
        localPlayerReadyStateSubject.onNext(true)

        // Then
        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Navigate to Home screen when user leaves the game`() {
        // Given
        createViewModel()
        viewModel.onStart()
        verify(exactly = 0) { mockNavigationBridge.navigate(any(), any()) }

        // When
        viewModel.leaveGame()

        // Then
        verify { mockNavigationBridge.navigate(Home, any()) }
        verify { inGameGuestFacade.leaveGame() }
    }

    @Test
    fun `User is notified when the game is canceled`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        gameEventSubject.onNext(GuestGameEvent.GameCanceled)

        // Then
        assertThat(viewModel.notifications.value).isEqualTo(WaitingRoomGuestNotification.NotifyGameCanceled)
    }

    @Test
    fun `User is notified when it is kicked off the game`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        gameEventSubject.onNext(GuestGameEvent.KickedOffGame)

        // Then
        assertThat(viewModel.notifications.value).isEqualTo(WaitingRoomGuestNotification.NotifyPlayerKickedOffGame)
    }

    @Test
    fun `Navigate to Home screen when user acknowledges that the game has been canceled`() {
        // Given
        createViewModel()
        viewModel.onStart()
        verify(exactly = 0) { mockNavigationBridge.navigate(any()) }

        // When
        gameEventSubject.onNext(GuestGameEvent.GameCanceled)
        viewModel.acknowledgeGameCanceled()

        // Then
        verify { mockNavigationBridge.navigate(Home, any()) }
    }

    @Test
    fun `Navigate to Home screen when user acknowledges being kicked off game`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        gameEventSubject.onNext(GuestGameEvent.KickedOffGame)
        viewModel.acknowledgeKickOffGame()

        // Then
        verify { mockNavigationBridge.navigate(Home, any()) }
    }

    @Test
    fun `Navigate to GameRoom when the game is launched`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        gameEventSubject.onNext(GuestGameEvent.GameLaunched)

        // Then
        verify { mockNavigationBridge.navigate(InGameGuestDestination.GameRoom, any()) }
    }

    private fun createViewModel() {
        viewModel = WaitingRoomGuestViewModel(
            mockWaitingRoomGuestFacade,
            communicationFacade,
            inGameGuestFacade,
            mockNavigationBridge,
            Schedulers.trampoline(),
            StubIdlingResource()
        )

        communicationStateSubject = PublishSubject.create()
        localPlayerReadyStateSubject = PublishSubject.create()
        gameEventSubject = PublishSubject.create()

        every { communicationFacade.currentCommunicationState() } returns communicationStateSubject
        every { mockWaitingRoomGuestFacade.observeLocalPlayerReadyState() } returns localPlayerReadyStateSubject
        every { inGameGuestFacade.observeGameEvents() } returns gameEventSubject
        every { inGameGuestFacade.leaveGame() } returns Completable.complete()
    }
}
