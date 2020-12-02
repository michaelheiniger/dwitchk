package ch.qscqlmpa.dwitchgame.gamediscovery


import ch.qscqlmpa.dwitchgame.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Maybe
import timber.log.Timber
import java.net.SocketException
import javax.inject.Inject

class TestNetworkAdapter @Inject
internal constructor() : NetworkAdapter {

    private val subject = PublishRelay.create<Packet>()

    fun setPacket(packet: Packet) {
        Timber.i("Feed network adapter with packet $packet")
        subject.accept(packet)
    }

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        // Nothing to do
    }

    override fun receive(): Maybe<Packet> {
        return subject.firstElement()
            .doOnSuccess { p -> Timber.i("Packet received: $p") }
            .doOnComplete { Timber.i("No packet received.") }
    }

    override fun close() {
        // Nothing to do
    }

}
