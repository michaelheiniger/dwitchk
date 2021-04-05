package ch.qscqlmpa.dwitchgame.gamediscovery.network

import org.tinylog.kotlin.Logger
import java.net.SocketException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TestNetworkAdapter @Inject internal constructor() : NetworkAdapter {

    private val blockingQueue = LinkedBlockingQueue<Packet>(10)

    fun setPacket(packet: Packet) {
        Logger.info { "Feed network adapter with packet $packet" }
        blockingQueue.offer(packet, 5, TimeUnit.SECONDS)
    }

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        // Nothing to do
    }

    override fun receive(): Packet {
        Logger.info("receive...")
        try {
            return blockingQueue.poll(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            throw SocketClosedException()
        }
    }

    override fun close() {
        // Nothing to do
    }
}
