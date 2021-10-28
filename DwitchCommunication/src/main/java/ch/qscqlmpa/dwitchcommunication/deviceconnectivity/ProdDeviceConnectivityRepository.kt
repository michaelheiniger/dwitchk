package ch.qscqlmpa.dwitchcommunication.deviceconnectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import java.net.Inet4Address
import javax.inject.Inject

internal class ProdDeviceConnectivityRepository @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : DeviceConnectivityRepository {

    private fun isLinkWifi(network: Network): Boolean =
        connectivityManager.getNetworkCapabilities(network)?.hasTransport(TRANSPORT_WIFI) ?: false

    private fun getLocalIpV4AddressIfAny(network: Network): String? {
        return connectivityManager.getLinkProperties(network)?.linkAddresses
            ?.map { l -> l.address }
            ?.filter { a -> !a.isLoopbackAddress && a is Inet4Address }
            ?.map { a -> a.hostAddress }
            ?.firstOrNull()
    }

    override fun observeConnectionState(): Observable<DeviceConnectionState> {
        return Observable.create { emitter ->
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    if (isLinkWifi(network)) {
                        val ipAddress = getLocalIpV4AddressIfAny(network)
                        if (ipAddress != null) {
                            Logger.debug { "Network available: connected to WLAN with IPv4: $ipAddress." }
                            emitter.onNext(DeviceConnectionState.ConnectedToWlan(ipAddress))
                        } else {
                            val addresses = connectivityManager.getLinkProperties(network)?.linkAddresses
                            Logger.debug { "Network available: device has no IPv4 address: $addresses." }
                            emitter.onNext(DeviceConnectionState.NotConnectedToWlan)
                        }
                    } else {
                        Logger.debug { "Network available: not connected to WLAN." }
                        emitter.onNext(DeviceConnectionState.NotConnectedToWlan)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Logger.debug { "Network lost: not connected to WLAN." }
                    emitter.onNext(DeviceConnectionState.NotConnectedToWlan)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    Logger.debug { "No WLAN network available." }
                    emitter.onNext(DeviceConnectionState.NotConnectedToWlan)
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            emitter.setCancellable { connectivityManager.unregisterNetworkCallback(callback) }
        }
    }
}
