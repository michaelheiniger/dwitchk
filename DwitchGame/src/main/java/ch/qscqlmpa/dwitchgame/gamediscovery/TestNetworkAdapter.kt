package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchgame.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import org.tinylog.kotlin.Logger
import java.net.SocketException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TestNetworkAdapter @Inject
internal constructor() : NetworkAdapter {

    private val blockingQueue = LinkedBlockingQueue<Packet>(10)

//    private var packet: Packet? = null

    fun setPacket(packet: Packet) {
        Logger.info { "Feed network adapter with packet $packet" }
        blockingQueue.offer(packet, 5, TimeUnit.SECONDS)
//        this.packet = packet
    }

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        // Nothing to do
    }

    override fun receive(): Packet {
        Logger.info("receive...")
        return blockingQueue.poll(50, TimeUnit.SECONDS)
    }

    override fun close() {
        // Nothing to do
    }
}
