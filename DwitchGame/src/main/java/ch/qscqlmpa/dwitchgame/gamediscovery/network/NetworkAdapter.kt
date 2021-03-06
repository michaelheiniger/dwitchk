package ch.qscqlmpa.dwitchgame.gamediscovery.network

import java.net.SocketException

interface NetworkAdapter {

    @Throws(SocketException::class)
    fun bind(listeningPort: Int)

    fun receive(): Packet

    fun close()
}
