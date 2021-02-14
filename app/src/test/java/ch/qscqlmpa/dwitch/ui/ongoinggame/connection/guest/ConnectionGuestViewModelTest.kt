package ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.common.Resource
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.model.UiInfoModel
import ch.qscqlmpa.dwitch.ui.model.Visibility
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GuestFacade
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class ConnectionGuestViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<GuestFacade>(relaxed = true)

    private lateinit var viewModel: ConnectionGuestViewModel

    private lateinit var communicationStateSubject: PublishSubject<GuestCommunicationState>

    @Before
    override fun setup() {
        super.setup()

        viewModel = ConnectionGuestViewModel(mockFacade, Schedulers.trampoline())

        communicationStateSubject = PublishSubject.create()

        every { mockFacade.currentCommunicationState() } returns communicationStateSubject
    }

    @Test
    fun `Reconnect action control is not displayed when communication state is Connected`() {
        val reconnectAction = viewModel.reconnectAction()
        subscribeToPublishers(reconnectAction)

        communicationStateSubject.onNext(GuestCommunicationState.Connected)

        assertThat(reconnectAction.value!!)
            .usingRecursiveComparison()
            .ignoringFields("enabled")
            .isEqualTo(UiControlModel(visibility = Visibility.Gone))
    }

    @Test
    fun `Reconnect action control is displayed when communication state is Disconnected`() {
        val reconnectAction = viewModel.reconnectAction()
        subscribeToPublishers(reconnectAction)

        communicationStateSubject.onNext(GuestCommunicationState.Disconnected)

        assertThat(reconnectAction.value!!)
            .usingRecursiveComparison()
            .ignoringFields("enabled")
            .isEqualTo(UiControlModel(visibility = Visibility.Visible))
    }

    @Test
    fun `Reconnect action control is displayed when communication state is Error`() {
        val reconnectAction = viewModel.reconnectAction()
        subscribeToPublishers(reconnectAction)

        communicationStateSubject.onNext(GuestCommunicationState.Error)

        assertThat(reconnectAction.value!!)
            .usingRecursiveComparison()
            .ignoringFields("enabled")
            .isEqualTo(UiControlModel(visibility = Visibility.Visible))
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

        assertThat(reconnectLoading.value!!)
            .usingRecursiveComparison()
            .ignoringFields("enabled")
            .isEqualTo(UiControlModel(visibility = Visibility.Visible))
    }

    @Test
    fun `Connection state info is updated whenever the connection state changes`() {
        val connectionStateInfo = viewModel.connectionStateInfo()
        subscribeToPublishers(connectionStateInfo)

        communicationStateSubject.onNext(GuestCommunicationState.Connected)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(Resource(R.string.connected_to_host)))

        communicationStateSubject.onNext(GuestCommunicationState.Disconnected)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(Resource(R.string.disconnected_from_host)))

        communicationStateSubject.onNext(GuestCommunicationState.Error)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(Resource(R.string.guest_connection_error)))
    }

    @Test
    fun `Reconnect action should forward call`() {
        viewModel.reconnect()
        verify { mockFacade.connect() }
    }

    private fun reconnectLoadingControlIsHiddenWHenCommunicationStateChanges(state: GuestCommunicationState) {
        val reconnectLoading = viewModel.reconnectLoading()
        subscribeToPublishers(reconnectLoading)

        viewModel.reconnect()

        assertThat(reconnectLoading.value!!)
            .usingRecursiveComparison()
            .ignoringFields("enabled")
            .isEqualTo(UiControlModel(visibility = Visibility.Visible))

        communicationStateSubject.onNext(state) // Change in communication state

        assertThat(reconnectLoading.value!!)
            .usingRecursiveComparison()
            .ignoringFields("enabled")
            .isEqualTo(UiControlModel(visibility = Visibility.Gone))
    }
}
