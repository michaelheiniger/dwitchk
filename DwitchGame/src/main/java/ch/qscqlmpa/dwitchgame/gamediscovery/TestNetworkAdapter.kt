package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchgame.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import mu.KLogging
import java.net.SocketException
import javax.inject.Inject

class TestNetworkAdapter @Inject
internal constructor() : NetworkAdapter {

    private var packet: Packet? = null

    fun setPacket(packet: Packet) {
        logger.info { "Feed network adapter with packet $packet" }
        this.packet = packet
    }

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        // Nothing to do
    }

    override fun receive(): Packet {
        return packet!!
    }

    override fun close() {
        // Nothing to do
    }

    companion object : KLogging()
}
