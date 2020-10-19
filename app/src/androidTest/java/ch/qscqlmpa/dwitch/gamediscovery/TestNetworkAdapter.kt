package ch.qscqlmpa.dwitch.gamediscovery


import ch.qscqlmpa.dwitch.gamediscovery.network.NetworkAdapter
import ch.qscqlmpa.dwitch.gamediscovery.network.Packet
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Maybe
import java.net.SocketException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestNetworkAdapter @Inject
internal constructor() : NetworkAdapter {

    private val subject = PublishRelay.create<Packet>()

    fun setPacket(packet: Packet) {
        subject.accept(packet)
    }

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        // Nothing to do
    }

    override fun receive(): Maybe<Packet> {
        return subject.firstElement()
    }

    override fun close() {
        // Nothing to do
    }

}
