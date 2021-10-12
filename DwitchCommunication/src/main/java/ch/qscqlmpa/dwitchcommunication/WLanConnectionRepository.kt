package ch.qscqlmpa.dwitchcommunication

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import ch.qscqlmpa.dwitchcommunication.di.CommunicationScope
import io.reactivex.rxjava3.core.Observable
import java.net.Inet4Address
import javax.inject.Inject

@CommunicationScope
class WLanConnectionRepository @Inject constructor(
    private val connectivityManager: ConnectivityManager
) {
    private fun isLinkWifi(network: Network): Boolean =
        connectivityManager.getNetworkCapabilities(network)?.hasTransport(TRANSPORT_WIFI) ?: false

    private fun getLocalIpV4AddressIfAny(network: Network): String? {
        return connectivityManager.getLinkProperties(network)?.linkAddresses
            ?.map { l -> l.address }
            ?.filter { a -> !a.isLoopbackAddress && a is Inet4Address }
            ?.map { a -> a.hostAddress }
            ?.first()
    }

    fun observeConnectionState(): Observable<DeviceConnectionState> {
        return Observable.create { emitter ->
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    if (isLinkWifi(network)) {
                        val ipAddress = getLocalIpV4AddressIfAny(network)
                        if (ipAddress != null) {
                            emitter.onNext(DeviceConnectionState.OnWifi(ipAddress))
                        } else {
                            emitter.onNext(DeviceConnectionState.Other)
                        }
                    } else {
                        emitter.onNext(DeviceConnectionState.Other)
                    }
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            emitter.setCancellable { connectivityManager.unregisterNetworkCallback(callback) }
        }
    }
}

sealed class DeviceConnectionState {
    data class OnWifi(val ipAddress: String) : DeviceConnectionState()
    object Other : DeviceConnectionState()
}
