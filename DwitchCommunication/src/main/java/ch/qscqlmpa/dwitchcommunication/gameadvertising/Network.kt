package ch.qscqlmpa.dwitchcommunication.gameadvertising

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

internal class Network @Inject constructor() {

    fun sendAdvertisement(destinationPort: Int, message: String) {
        DatagramSocket().use { datagramSocket ->
            val buffer = message.toByteArray()
            val destinationAddress = InetAddress.getByName("255.255.255.255")
            val packet = DatagramPacket(
                buffer,
                buffer.size,
                destinationAddress,
                destinationPort
            )
            datagramSocket.send(packet)
        }
    }
}
