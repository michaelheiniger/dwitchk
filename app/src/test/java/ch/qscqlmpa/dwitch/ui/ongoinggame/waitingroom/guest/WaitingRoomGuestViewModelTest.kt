package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import ch.qscqlmpa.dwitch.app.ProdIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomGuestFacade
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class) // Needed because of logging
class WaitingRoomGuestViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomGuestFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomGuestViewModel

    private lateinit var communicationStateSubject: PublishSubject<GuestCommunicationState>
    private lateinit var localPlayerReadyStateSubject: PublishSubject<Boolean>
    private lateinit var gameEventSubject: PublishSubject<GuestGameEvent>

    @Before
    fun setup() {
        viewModel = WaitingRoomGuestViewModel(mockFacade, Schedulers.trampoline(), ProdIdlingResource())

        communicationStateSubject = PublishSubject.create()
        localPlayerReadyStateSubject = PublishSubject.create()
        gameEventSubject = PublishSubject.create()

        every { mockFacade.observeCommunicationState() } returns communicationStateSubject
        every { mockFacade.observeLocalPlayerReadyState() } returns localPlayerReadyStateSubject
        every { mockFacade.observeGameEvents() } returns gameEventSubject
        every { mockFacade.leaveGame() } returns Completable.complete()
        viewModel.onStart()
    }

    @Test
    fun `Local player ready control is initially disabled and unchecked`() {
        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Local player ready control is enabled and checked when communication state is Connected and player is ready`() {
        communicationStateSubject.onNext(GuestCommunicationState.Connected)
        localPlayerReadyStateSubject.onNext(true)

        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = true, checked = true))
    }

    @Test
    fun `Local player ready state control is enabled and unchecked when communication state is Connected and player is not ready`() {
        communicationStateSubject.onNext(GuestCommunicationState.Connected)
        localPlayerReadyStateSubject.onNext(false)

        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = true, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Connecting`() {
        communicationStateSubject.onNext(GuestCommunicationState.Connecting)
        localPlayerReadyStateSubject.onNext(true)

        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Disconnected`() {
        communicationStateSubject.onNext(GuestCommunicationState.Disconnected)
        localPlayerReadyStateSubject.onNext(true)

        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Local player ready state control is disabled and unchecked when communication state is Error`() {
        communicationStateSubject.onNext(GuestCommunicationState.Error)
        localPlayerReadyStateSubject.onNext(true)

        assertThat(viewModel.ready.value).isEqualTo(UiCheckboxModel(enabled = false, checked = false))
    }

    @Test
    fun `Navigate to Home screen when user acknowledges that the game has been canceled`() {
        assertThat(viewModel.navigation.value).isNull()

        viewModel.acknowledgeGameCanceledEvent()

        assertThat(viewModel.navigation.value).isEqualTo(WaitingRoomGuestDestination.NavigateToHomeScreen)
    }

    @Test
    fun `Navigate to Home screen when user leaves the game`() {
        assertThat(viewModel.navigation.value).isNull()

        viewModel.leaveGame()

        assertThat(viewModel.navigation.value).isEqualTo(WaitingRoomGuestDestination.NavigateToHomeScreen)
        verify { mockFacade.leaveGame() }
    }

    @Test
    fun `User is notified when GameCanceled event occurs`() {
        gameEventSubject.onNext(GuestGameEvent.GameCanceled)

        assertThat(viewModel.notifications.value).isEqualTo(WaitingRoomGuestNotification.NotifyGameCanceled)
    }

    @Test
    fun `User is notified when KickedOffGame event occurs`() {
        gameEventSubject.onNext(GuestGameEvent.KickedOffGame)

        assertThat(viewModel.notifications.value).isEqualTo(WaitingRoomGuestNotification.NotifyPlayerKickedOffGame)
    }

    @Test
    fun `Navigate to Home screen when user acknowledges GameCanceled event`() {
        gameEventSubject.onNext(GuestGameEvent.GameCanceled)

        viewModel.acknowledgeGameCanceledEvent()

        assertThat(viewModel.navigation.value).isEqualTo(WaitingRoomGuestDestination.NavigateToHomeScreen)
    }

    @Test
    fun `Navigate to Home screen when user acknowledges KickedOffGame event`() {
        gameEventSubject.onNext(GuestGameEvent.KickedOffGame)

        viewModel.acknowledgeKickOffGame()

        assertThat(viewModel.navigation.value).isEqualTo(WaitingRoomGuestDestination.NavigateToHomeScreen)
    }
}
