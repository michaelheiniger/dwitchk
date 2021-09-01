package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest

import ch.qscqlmpa.dwitch.app.StubIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
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

    private val mockFacade = mockk<WaitingRoomGuestFacade>(relaxed = true)
    private val mockNavigationBridge = mockk<NavigationBridge>(relaxed = true)

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
        communicationStateSubject.onNext(GuestCommunicationState.Disconnected)
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
        communicationStateSubject.onNext(GuestCommunicationState.Error)
        localPlayerReadyStateSubject.onNext(true)

        // Then
        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Navigate to Home screen when user leaves the game`() {
        // Given
        createViewModel()
        viewModel.onStart()
        verify(exactly = 0) { mockNavigationBridge.navigate(any()) }

        // When
        viewModel.leaveGame()

        // Then
        verify { mockNavigationBridge.navigate(Destination.HomeScreens.Home) }
        verify { mockFacade.leaveGame() }
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
        verify { mockNavigationBridge.navigate(Destination.HomeScreens.Home) }
    }

    @Test
    fun `Navigate to Home screen when user acknowledges being kicked off game`() {
        // Given
        createViewModel()
        viewModel.onStart()

        //When
        gameEventSubject.onNext(GuestGameEvent.KickedOffGame)
        viewModel.acknowledgeKickOffGame()

        // Then
        verify { mockNavigationBridge.navigate(Destination.HomeScreens.Home) }
    }

    @Test
    fun `Navigate to GameRoom when the game is launched`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        gameEventSubject.onNext(GuestGameEvent.GameLaunched)

        // Then
        verify { mockNavigationBridge.navigate(Destination.GameScreens.GameRoomGuest) }
    }

    private fun createViewModel() {
        viewModel = WaitingRoomGuestViewModel(
            mockFacade,
            gameAdvertisingFacade = mockk(relaxed = true),
            mockNavigationBridge,
            Schedulers.trampoline(),
            StubIdlingResource()
        )

        communicationStateSubject = PublishSubject.create()
        localPlayerReadyStateSubject = PublishSubject.create()
        gameEventSubject = PublishSubject.create()

        every { mockFacade.observeCommunicationState() } returns communicationStateSubject
        every { mockFacade.observeLocalPlayerReadyState() } returns localPlayerReadyStateSubject
        every { mockFacade.observeGameEvents() } returns gameEventSubject
        every { mockFacade.leaveGame() } returns Completable.complete()
    }

}
