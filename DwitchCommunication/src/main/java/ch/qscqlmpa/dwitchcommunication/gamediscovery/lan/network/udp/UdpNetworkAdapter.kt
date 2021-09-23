package ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.udp

import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.NetworkAdapter
import ch.qscqlmpa.dwitchcommunication.gamediscovery.lan.network.Packet
import org.tinylog.kotlin.Logger
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import javax.inject.Inject

internal class UdpNetworkAdapter @Inject constructor() : NetworkAdapter {

    private lateinit var receiveSocket: DatagramSocket

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        Logger.debug { "Opening socket..." }
        receiveSocket = DatagramSocket(listeningPort)
    }

    override fun receive(): Packet {
        val receiveData = ByteArray(1024)
        val receivePacket = DatagramPacket(receiveData, receiveData.size)

        try {
            receiveSocket.receive(receivePacket) // Blocking call
            Logger.debug { "Packet received: ${receivePacket.data}" }
        } catch (e: SocketException) {
            throw SocketClosedException()
        }

        val message = String(receivePacket.data).substring(0, receivePacket.length)
        val senderIpAddress = receivePacket.address?.hostAddress ?: ""
        val senderPort = receivePacket.port
        return Packet(message, senderIpAddress, senderPort)
    }

    override fun close() {
        Logger.debug { "Closing socket..." }
        receiveSocket.close()
    }
}
