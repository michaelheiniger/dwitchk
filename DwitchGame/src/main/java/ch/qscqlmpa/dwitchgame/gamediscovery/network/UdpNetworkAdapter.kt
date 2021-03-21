package ch.qscqlmpa.dwitchgame.gamediscovery.network

import mu.KLogging
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import javax.inject.Inject

class UdpNetworkAdapter @Inject constructor() : NetworkAdapter {

    private lateinit var receiveSocket: DatagramSocket

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        logger.debug { "Opening socket..." }
        logger.isDebugEnabled
        receiveSocket = DatagramSocket(listeningPort)
    }

    override fun receive(): Packet {
        val receiveData = ByteArray(1024)
        val receivePacket = DatagramPacket(receiveData, receiveData.size)

        receiveSocket.receive(receivePacket) // Blocking call
        logger.debug { "Packet received: ${receivePacket.data}" }

        val message = String(receivePacket.data).substring(0, receivePacket.length)
        val senderIpAddress = receivePacket.address.hostAddress
        val senderPort = receivePacket.port
        return Packet(message, senderIpAddress, senderPort)
    }

    override fun close() {
        logger.debug { "Closing socket..." }
        receiveSocket.close()
    }

    companion object : KLogging()
}
