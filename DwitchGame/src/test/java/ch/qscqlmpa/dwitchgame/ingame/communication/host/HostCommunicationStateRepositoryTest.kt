package ch.qscqlmpa.dwitchgame.ingame.communication.host

import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectionState
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HostCommunicationStateRepositoryTest : BaseUnitTest() {

    private val mockDeviceConnectivityRepository = mockk<DeviceConnectivityRepository>(relaxed = true)

    private lateinit var connectionStateSubject: PublishSubject<DeviceConnectionState>

    private lateinit var hostCommunicationStateRepository: HostCommunicationStateRepository

    @BeforeEach
    fun setup() {
        connectionStateSubject = PublishSubject.create()
        every { mockDeviceConnectivityRepository.observeConnectionState() } returns connectionStateSubject

        hostCommunicationStateRepository = HostCommunicationStateRepository(mockDeviceConnectivityRepository)
    }

    @Test
    fun `When event 'listening for connections' is received, the current communication state becomes 'open'`() {
        // Given
        val currentStateObserver = hostCommunicationStateRepository.currentState().test()
        emitDeviceIsConnectedToWlan()
        currentStateObserver.assertValueAt(0, HostCommunicationState.OfflineDisconnected(connectedToWlan = true))

        // When
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.ListeningForConnections)

        // Then
        currentStateObserver.assertValueAt(1, HostCommunicationState.Online)
    }

    @Test
    fun `When event 'listening for connections' is received, the guest is connected to the game`() {
        // Given
        val connectedToGameObserver = hostCommunicationStateRepository.connectedToGame().test()
        emitDeviceIsConnectedToWlan()
        connectedToGameObserver.assertValueAt(0, false)

        // When
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.ListeningForConnections)

        // Then
        connectedToGameObserver.assertValueAt(1, true)
    }

    @Test
    fun `When event 'starting server' is received, the current communication state becomes 'starting'`() {
        // Given
        val currentStateObserver = hostCommunicationStateRepository.currentState().test()
        emitDeviceIsConnectedToWlan()
        currentStateObserver.assertValueAt(0, HostCommunicationState.OfflineDisconnected(connectedToWlan = true))

        // When
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.StartingServer)

        // Then
        currentStateObserver.assertValueAt(1, HostCommunicationState.Starting)
    }

    @Test
    fun `When event 'starting server' is received, the host is NOT connected to the game`() {
        // Given
        val connectedToGameObserver = hostCommunicationStateRepository.connectedToGame().test()
        emitDeviceIsConnectedToWlan()
        connectedToGameObserver.assertValueAt(0, false)

        // When
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.StartingServer)

        // Then
        connectedToGameObserver.assertValueAt(1, false)
    }

    @Test
    fun `When event 'error listening for connections' is received, the current communication state becomes 'offline failed'`() {
        // Given
        val currentStateObserver = hostCommunicationStateRepository.currentState().test()
        emitDeviceIsConnectedToWlan()
        currentStateObserver.assertValueAt(0, HostCommunicationState.OfflineDisconnected(connectedToWlan = true))

        // When
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.ErrorListeningForConnections(null))

        // Then
        currentStateObserver.assertValueAt(1, HostCommunicationState.OfflineFailed(connectedToWlan = true))

        // When
        emitDeviceIsNotConnectedToWlan()

        // Then
        currentStateObserver.assertValueAt(2, HostCommunicationState.OfflineFailed(connectedToWlan = false))
    }

    @Test
    fun `When event 'error listening for connections' is received, the guest is NOT connected to the game`() {
        // Given
        val connectedToGameObserver = hostCommunicationStateRepository.connectedToGame().test()
        emitDeviceIsNotConnectedToWlan()
        connectedToGameObserver.assertValueAt(0, false)

        // When
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.ErrorListeningForConnections(null))
        emitDeviceIsConnectedToWlan()

        // Then
        connectedToGameObserver.assertValueAt(1, false) // Host can be on WLAN without being connected to the game
    }

    @Test
    fun `When event 'stopped listening for connections' is received, the current communication state becomes 'offline disconnected'`() {
        // Given
        val currentStateObserver = hostCommunicationStateRepository.currentState().test()
        emitDeviceIsConnectedToWlan()
        currentStateObserver.assertValueAt(0, HostCommunicationState.OfflineDisconnected(connectedToWlan = true))
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.ListeningForConnections)
        currentStateObserver.assertValueAt(1, HostCommunicationState.Online)

        // When
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.StoppedListeningForConnections)

        // Then
        currentStateObserver.assertValueAt(2, HostCommunicationState.OfflineDisconnected(connectedToWlan = true))

        // When
        emitDeviceIsNotConnectedToWlan()

        // Then
        currentStateObserver.assertValueAt(3, HostCommunicationState.OfflineDisconnected(connectedToWlan = false))
    }

    @Test
    fun `When event 'stopped listening for connections' is received, the host is NOT connected to the game`() {
        // Given
        val connectedToGameObserver = hostCommunicationStateRepository.connectedToGame().test()
        emitDeviceIsConnectedToWlan()
        connectedToGameObserver.assertValueAt(0, false)
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.ListeningForConnections)
        connectedToGameObserver.assertValueAt(1, true)

        // When
        hostCommunicationStateRepository.notifyEvent(ServerEvent.CommunicationEvent.StoppedListeningForConnections)

        // Then
        connectedToGameObserver.assertValueAt(2, false) // Host can be on WLAN without being connected to the game
    }

    private fun emitDeviceIsConnectedToWlan() {
        connectionStateSubject.onNext(DeviceConnectionState.ConnectedToWlan("192.168.1.245"))
    }

    private fun emitDeviceIsNotConnectedToWlan() {
        connectionStateSubject.onNext(DeviceConnectionState.NotConnectedToWlan)
    }
}
