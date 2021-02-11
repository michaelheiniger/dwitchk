package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchgame.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Maybe
import mu.KLogging
import java.net.SocketException
import javax.inject.Inject

class TestNetworkAdapter @Inject
internal constructor() : NetworkAdapter {

    private val subject = PublishRelay.create<Packet>()

    fun setPacket(packet: Packet) {
        logger.info { "Feed network adapter with packet $packet" }
        subject.accept(packet)
    }

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        // Nothing to do
    }

    override fun receive(): Maybe<Packet> {
        return subject.firstElement()
            .doOnSuccess { p -> logger.info { "Packet received: $p" } }
            .doOnComplete { logger.info { "No packet received." } }
    }

    override fun close() {
        // Nothing to do
    }

    companion object : KLogging()
}
