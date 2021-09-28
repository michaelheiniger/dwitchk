package ch.qscqlmpa.dwitchcommunication.gamediscovery.lan

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.common.ApplicationConfigRepository
import ch.qscqlmpa.dwitchcommunication.common.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.NetworkAdapter
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.Packet
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.udp.SocketClosedException
import io.reactivex.rxjava3.core.Observable
import org.joda.time.LocalDateTime
import org.tinylog.kotlin.Logger
import java.net.SocketException
import javax.inject.Inject

internal class LanGameDiscovery @Inject constructor(
    applicationConfigRepository: ApplicationConfigRepository,
    private val serializerFactory: SerializerFactory,
    private val networkAdapter: NetworkAdapter
) : GameDiscovery {

    private val listeningPort = applicationConfigRepository.config.gameAdvertising.port
    private var isListening = true

    @Suppress("SwallowedException")
    override fun listenForAdvertisedGames(): Observable<GameAdvertisingInfo> {
        Logger.info { "Listen for advertised games..." }
        isListening = true

        return Observable.create { observer ->
            try {
                networkAdapter.bind(listeningPort)
            } catch (e: SocketException) {
                observer.onError(e)
            }

            observer.setCancellable { networkAdapter.close() }

            while (isListening) {
                try {
                    val advertisedGame = buildAdvertisedGame(networkAdapter.receive())
                    observer.onNext(advertisedGame)
                } catch (e: SocketClosedException) {
                    isListening = false
                }
            }
        }
    }

    private fun buildAdvertisedGame(packet: Packet): GameAdvertisingInfo {
        Logger.trace { "Packet received: $packet" }
        return serializerFactory.unserializeGameInfo(packet.message)
            .copy(discoveryTime = LocalDateTime.now())
    }
}
