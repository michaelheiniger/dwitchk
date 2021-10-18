package ch.qscqlmpa.dwitchcommunication.deviceconnectivity

import io.reactivex.rxjava3.core.Observable

interface DeviceConnectivityRepository {
    fun observeConnectionState(): Observable<DeviceConnectionState>
}

sealed class DeviceConnectionState {
    data class ConnectedToWlan(val ipAddress: String) : DeviceConnectionState()
    object NotConnectedToWlan : DeviceConnectionState()
}
