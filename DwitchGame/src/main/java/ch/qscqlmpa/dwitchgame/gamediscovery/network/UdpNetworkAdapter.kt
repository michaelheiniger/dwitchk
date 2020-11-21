package ch.qscqlmpa.dwitchgame.gamediscovery.network

import io.reactivex.Maybe
import timber.log.Timber
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import javax.inject.Inject

class UdpNetworkAdapter @Inject constructor() : NetworkAdapter {

    private lateinit var receiveSocket: DatagramSocket

    @Throws(SocketException::class)
    override fun bind(listeningPort: Int) {
        receiveSocket = DatagramSocket(listeningPort)
    }

    override fun receive(): Maybe<Packet> {

        return Maybe.fromCallable {
            val receiveData = ByteArray(1024)
            val receivePacket = DatagramPacket(receiveData, receiveData.size)

            try {
                receiveSocket.receive(receivePacket) // Blocking call
            } catch (e: SocketException) {
                Timber.d("Socket closed")
                return@fromCallable null
            }

            val message = String(receivePacket.data).substring(0, receivePacket.length)
            val senderIpAddress = receivePacket.address.hostAddress
            val senderPort = receivePacket.port

            Packet(message, senderIpAddress, senderPort)
        }
    }

    override fun close() {
//        receiveSocket.disconnect() //TODO: Is that needed ? It makes the app freeze.
        receiveSocket.close()
    }
}
