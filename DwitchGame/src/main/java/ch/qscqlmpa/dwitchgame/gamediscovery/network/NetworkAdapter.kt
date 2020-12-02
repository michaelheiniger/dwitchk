package ch.qscqlmpa.dwitchgame.gamediscovery.network

import io.reactivex.rxjava3.core.Maybe
import java.net.SocketException

interface NetworkAdapter {

    @Throws(SocketException::class)
    fun bind(listeningPort: Int)

    fun receive(): Maybe<Packet>

    fun close()
}
