package ch.qscqlmpa.dwitch.gamediscovery.network

import io.reactivex.Maybe
import java.net.SocketException

interface NetworkAdapter {

    @Throws(SocketException::class)
    fun bind(listeningPort: Int)

    fun receive(): Maybe<Packet>

    fun close()
}
