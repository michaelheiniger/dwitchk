package ch.qscqlmpa.dwitchgame.gamediscovery.network

import ch.qscqlmpa.dwitchgame.gameadvertising.SerializerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscovery
import io.reactivex.rxjava3.core.Observable
import org.joda.time.LocalTime
import org.tinylog.kotlin.Logger
import java.net.SocketException
import javax.inject.Inject

class LanGameDiscovery @Inject constructor(
    private val serializerFactory: SerializerFactory,
    private val networkAdapter: NetworkAdapter
) : GameDiscovery {

    private var isListening = true

    override fun listenForAdvertisedGames(): Observable<AdvertisedGame> {

        Logger.info { "Listen for advertised games..." }
        isListening = true

        val listeningPort = 8888

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

    private fun buildAdvertisedGame(packet: Packet): AdvertisedGame {
        Logger.trace { "Packet received: $packet" }
        val gameInfo = serializerFactory.unserializeGameInfo(packet.message)
        return AdvertisedGame(
            gameInfo.isNew,
            gameInfo.gameName,
            gameInfo.gameCommonId,
            packet.senderIpAddress,
            gameInfo.gamePort,
            LocalTime.now()
        )
    }
}
