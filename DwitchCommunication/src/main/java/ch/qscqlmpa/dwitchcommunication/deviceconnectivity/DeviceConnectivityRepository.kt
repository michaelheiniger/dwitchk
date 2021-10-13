package ch.qscqlmpa.dwitchcommunication.deviceconnectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkRequest
import ch.qscqlmpa.dwitchcommunication.di.CommunicationScope
import io.reactivex.rxjava3.core.Observable
import java.net.Inet4Address
import javax.inject.Inject

@CommunicationScope
class DeviceConnectivityRepository @Inject constructor(
    private val connectivityManager: ConnectivityManager
) {

    private val wifiNetworkRequest = NetworkRequest.Builder()
        .addTransportType(TRANSPORT_WIFI)
        .build()

    private fun getLocalIpV4AddressIfAny(network: Network): String? {
        return connectivityManager.getLinkProperties(network)?.linkAddresses
            ?.map { l -> l.address }
            ?.filter { a -> !a.isLoopbackAddress && a is Inet4Address }
            ?.map { a -> a.hostAddress }
            ?.firstOrNull()
    }

    fun observeConnectionState(): Observable<DeviceConnectionState> {
        return Observable.create { emitter ->

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    val ipAddress = getLocalIpV4AddressIfAny(network)
                    if (ipAddress != null) {
                        emitter.onNext(DeviceConnectionState.OnWifi(ipAddress))
                    } else {
                        emitter.onNext(DeviceConnectionState.NotOnWifi)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    emitter.onNext(DeviceConnectionState.NotOnWifi)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    emitter.onNext(DeviceConnectionState.NotOnWifi)
                }
            }
            connectivityManager.registerNetworkCallback(wifiNetworkRequest, callback)
            emitter.setCancellable { connectivityManager.unregisterNetworkCallback(callback) }
        }
    }
}

sealed class DeviceConnectionState {
    data class OnWifi(val ipAddress: String) : DeviceConnectionState()
    object NotOnWifi : DeviceConnectionState()
}
