package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectionState
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GuestCommunicationStateRepositoryTest : BaseUnitTest() {

    private val mockDeviceConnectivityRepository = mockk<DeviceConnectivityRepository>(relaxed = true)

    private lateinit var connectionStateSubject: PublishSubject<DeviceConnectionState>

    private lateinit var guestCommunicationStateRepository: GuestCommunicationStateRepository

    @BeforeEach
    fun setup() {
        connectionStateSubject = PublishSubject.create()
        every { mockDeviceConnectivityRepository.observeConnectionState() } returns connectionStateSubject

        guestCommunicationStateRepository = GuestCommunicationStateRepository(mockDeviceConnectivityRepository)
    }

    @Test
    fun `When event 'connected to server' is received, the current communication state becomes 'connected'`() {
        // Given
        val currentStateObserver = guestCommunicationStateRepository.currentState().test()
        emitDeviceIsConnectedToWlan()
        currentStateObserver.assertValueAt(0, GuestCommunicationState.Disconnected(connectedToWlan = true))

        // When
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectedToServer)

        // Then
        currentStateObserver.assertValueAt(1, GuestCommunicationState.Connected)
    }

    @Test
    fun `When event 'connected to server' is received, the guest is connected to the host`() {
        // Given
        val connectedToHostObserver = guestCommunicationStateRepository.connectedToHost().test()
        emitDeviceIsConnectedToWlan()
        connectedToHostObserver.assertValueAt(0, false)

        // When
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectedToServer)

        // Then
        connectedToHostObserver.assertValueAt(1, true)
    }

    @Test
    fun `When event 'connecting to server' is received, the current communication state becomes 'connecting'`() {
        // Given
        val currentStateObserver = guestCommunicationStateRepository.currentState().test()
        emitDeviceIsConnectedToWlan()
        currentStateObserver.assertValueAt(0, GuestCommunicationState.Disconnected(connectedToWlan = true))

        // When
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectingToServer)

        // Then
        currentStateObserver.assertValueAt(1, GuestCommunicationState.Connecting)
    }

    @Test
    fun `When event 'connecting to server' is received, the guest is NOT connected to the host`() {
        // Given
        val connectedToHostObserver = guestCommunicationStateRepository.connectedToHost().test()
        emitDeviceIsConnectedToWlan()
        connectedToHostObserver.assertValueAt(0, false)

        // When
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectingToServer)

        // Then
        connectedToHostObserver.assertValueAt(1, false)
    }

    @Test
    fun `When event 'connection error' is received, the current communication state becomes 'error'`() {
        // Given
        val currentStateObserver = guestCommunicationStateRepository.currentState().test()
        emitDeviceIsConnectedToWlan()
        currentStateObserver.assertValueAt(0, GuestCommunicationState.Disconnected(connectedToWlan = true))

        // When
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectionError("error"))

        // Then
        currentStateObserver.assertValueAt(1, GuestCommunicationState.Error(connectedToWlan = true))

        // When
        emitDeviceIsNotConnectedToWlan()

        // Then
        currentStateObserver.assertValueAt(2, GuestCommunicationState.Error(connectedToWlan = false))
    }

    @Test
    fun `When event 'connection error' is received, the guest is NOT connected to the host`() {
        // Given
        val connectedToHostObserver = guestCommunicationStateRepository.connectedToHost().test()
        emitDeviceIsNotConnectedToWlan()
        connectedToHostObserver.assertValueAt(0, false)

        // When
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectionError("error"))

        // Then
        connectedToHostObserver.assertValueAt(1, false)

        // When
        emitDeviceIsConnectedToWlan()

        // Then
        connectedToHostObserver.assertValueAt(2, false) // Guest can be on WLAN without being connected to Host
    }

    @Test
    fun `When event 'disconnected' is received, the current communication state becomes 'disconnected from the host'`() {
        // Given
        val currentStateObserver = guestCommunicationStateRepository.currentState().test()
        emitDeviceIsConnectedToWlan()
        currentStateObserver.assertValueAt(0, GuestCommunicationState.Disconnected(connectedToWlan = true))
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectedToServer)
        currentStateObserver.assertValueAt(1, GuestCommunicationState.Connected)

        // When
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.DisconnectedFromServer)

        // Then
        currentStateObserver.assertValueAt(2, GuestCommunicationState.Disconnected(connectedToWlan = true))

        // When
        emitDeviceIsNotConnectedToWlan()

        // Then
        currentStateObserver.assertValueAt(3, GuestCommunicationState.Disconnected(connectedToWlan = false))
    }

    @Test
    fun `When event 'disconnected' is received, the guest is NOT connected to the host`() {
        // Given
        val connectedToHostObserver = guestCommunicationStateRepository.connectedToHost().test()
        emitDeviceIsNotConnectedToWlan()
        connectedToHostObserver.assertValueAt(0, false)
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.ConnectedToServer)
        connectedToHostObserver.assertValueAt(1, true)

        // When
        guestCommunicationStateRepository.notifyEvent(ClientEvent.CommunicationEvent.DisconnectedFromServer)

        // Then
        connectedToHostObserver.assertValueAt(2, false)

        // When
        emitDeviceIsConnectedToWlan()

        // Then
        connectedToHostObserver.assertValueAt(3, false) // Guest can be on WLAN without being connected to Host
    }

    private fun emitDeviceIsConnectedToWlan() {
        connectionStateSubject.onNext(DeviceConnectionState.OnWifi("192.168.1.245"))
    }

    private fun emitDeviceIsNotConnectedToWlan() {
        connectionStateSubject.onNext(DeviceConnectionState.NotOnWifi)
    }
}
