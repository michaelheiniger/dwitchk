package ch.qscqlmpa.dwitch.gamediscovery.network


import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.gamediscovery.GameDiscovery
import io.reactivex.Observable
import org.joda.time.LocalTime
import timber.log.Timber
import java.net.SocketException
import javax.inject.Inject

class LanGameDiscovery @Inject
constructor(private val networkAdapter: NetworkAdapter) : GameDiscovery {

    private var isListening = false

    override fun listenForAdvertisedGame(): Observable<AdvertisedGame> {

        Timber.i("Listen for advertised games...")

        val listeningPort = 8888
        isListening = true

        try {
            networkAdapter.bind(listeningPort)
        } catch (e: SocketException) {
            return Observable.error(e)
        }

        return networkAdapter.receive()
                .doOnError { e -> Timber.e(e, "Error listening for advertised game") }
                .map { packet ->
                    Timber.d("Packet received: %s", packet)
                    AdvertisedGame(
                            packet.message,
                            packet.senderIpAddress,
                            packet.senderPort,
                            LocalTime.now())
                }.repeatUntil { !isListening }
                .toObservable()
    }

    override fun stopListening() {
        Timber.i("Stop listening")
        isListening = false
        networkAdapter.close()
    }
}
