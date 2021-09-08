package ch.qscqlmpa.dwitchgame.gamediscovery.lan

import ch.qscqlmpa.dwitchgame.common.ApplicationConfigRepository
import ch.qscqlmpa.dwitchgame.gameadvertising.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gameadvertising.SerializerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.Packet
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.udp.SocketClosedException
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

    override fun listenForAdvertisedGames(): Observable<AdvertisedGame> {

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

    private fun buildAdvertisedGame(packet: Packet): AdvertisedGame {
        Logger.trace { "Packet received: $packet" }
        val gameInfo = serializerFactory.unserializeGameInfo(packet.message)
        return AdvertisedGame(
            gameInfo.isNew,
            gameInfo.gameName,
            gameInfo.gameCommonId,
            packet.senderIpAddress,
            gameInfo.gamePort,
            LocalDateTime.now()
        )
    }
}
