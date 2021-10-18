package ch.qscqlmpa.dwitchcommunication.deviceconnectivity

import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class TestDeviceConnectivityRepository @Inject constructor() : DeviceConnectivityRepository {

    private val currentStateRelay = BehaviorRelay.create<DeviceConnectionState>()

    fun publishNewState(state: DeviceConnectionState) {
        currentStateRelay.accept(state)
    }

    override fun observeConnectionState(): Observable<DeviceConnectionState> {
        return currentStateRelay
    }
}
